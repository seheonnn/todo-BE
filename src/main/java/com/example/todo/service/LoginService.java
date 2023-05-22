package com.example.todo.service;

import com.example.todo.config.RoleType;
import com.example.todo.config.security.JwtTokenProvider;
import com.example.todo.dto.ChangePwInfo;
import com.example.todo.dto.TokenDTO;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();



    public UserEntity join(UserDTO user) throws Exception {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(user.getEmail());
        if(userEntityOptional.isPresent()){
            UserEntity userEntity = userEntityOptional.get();
            log.info(String.valueOf(userEntity.getStatus()));

            if ('D'==userEntity.getStatus()) {
                String encryptedPw = encoder.encode(user.getPassword());
                userEntity.setPassword(encryptedPw);
                userEntity.setStatus('A');
                return userRepository.saveAndFlush(userEntity);
            }
            else
                throw new Exception("이미 존재하는 이메일입니다.");
        }
        String encryptedPw = encoder.encode(user.getPassword());
        UserEntity newUser = UserEntity.builder()
                .email(user.getEmail())
                .password(encryptedPw)
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .status('A')
                .role(String.valueOf(RoleType.USER))
                .login_cnt(0L)
                .created_at(new Timestamp(System.currentTimeMillis()).toLocalDateTime())
                .build();
        return userRepository.saveAndFlush(newUser);
    }

    public List<TokenDTO> login(UserDTO user) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new Exception("가입되지 않은 이메일입니다."));
        if (!encoder.matches(user.getPassword(), userEntity.getPassword())) {
            throw new Exception("잘못된 비밀번호입니다.");
        }
        if (userEntity.getStatus() == 'D')
            throw new Exception("탈퇴된 사용자입니다.");
        userEntity.setLogin_at(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        userEntity.setLogin_cnt(userEntity.getLogin_cnt()+1);
        userRepository.saveAndFlush(userEntity);

        // token 발급
        TokenDTO refreshToken = jwtTokenProvider.createRefreshToken();
        TokenDTO accessToken = jwtTokenProvider.createAccessToken(userEntity.getEmail(), userEntity.getRole());

        // Redis 에 RTL user@email.com(key) : ----token-----(value) 형태로 token 저장
        redisTemplate.opsForValue().set("RT:"+userEntity.getEmail(), accessToken.getToken(), accessToken.getRefreshTokenExpiresTime().getTime(), TimeUnit.MILLISECONDS);
//        String responseBody = "{\"atk\":\"" + accessToken.getToken() + "\", \"rtk\":\"" + refreshToken.getToken() + "\"}";
        List<TokenDTO> tokenDTOList = new ArrayList<>();
        tokenDTOList.add(refreshToken);
        tokenDTOList.add(accessToken);
        return tokenDTOList;
    }

    public String logout(HttpServletRequest request) {
        try {
            // token 으로 user 정보 받음
            String email = jwtTokenProvider.getCurrentUser(request);
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

            user.setLogin_cnt(0L);
            userRepository.saveAndFlush(user);

            // Redis 에서 해당 User email 로 저장된 token 이 있는지 확인 후 있는 경우 삭제
            Object token = redisTemplate.opsForValue().get("RT:" + user.getEmail());
            if (token != null) {
                redisTemplate.delete("RT:"+user.getEmail());
            }

            Long expire = jwtTokenProvider.getExpireTime((String) token).getTime();
            redisTemplate.opsForValue().set(token, "logout", expire, TimeUnit.MILLISECONDS);

            return "로그아웃 성공";
        } catch (Exception exception) {
//            return exception.getMessage();
            return "유효하지 않은 토큰입니다.";
        }
    }

    public boolean validatePw(ChangePwInfo changePwInfo, HttpServletRequest request) throws Exception {
        String email = jwtTokenProvider.getCurrentUser(request);
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        if (encoder.matches(changePwInfo.getOriginalPw(), userEntity.getPassword()))
            return true;
        else throw new Exception("비밀번호 불일치");
    }

    public Optional<UserEntity> changePw(ChangePwInfo changePwInfo, HttpServletRequest request) throws Exception {
        String email = jwtTokenProvider.getCurrentUser(request);
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        String encryptedPw = encoder.encode(changePwInfo.getNewPw());
        if (changePwInfo.getNewPw().equals(changePwInfo.getNewPwCheck())) {
            userEntity.setPassword(encryptedPw);
            return Optional.of(userRepository.saveAndFlush(userEntity));
        }
        else throw new Exception("비밀번호 재확인");
    }

    public String findPw(UserDTO user) throws Exception {
        // 이메일 확인
        UserEntity userEntity = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new Exception("가입되지 않은 이메일입니다."));
        // 이름 확인
        if (!userEntity.getName().equals(user.getName())) {
            throw new Exception("일치하는 사용자 정보가 없습니다.");
        }
        Random r = new Random();
        int num = r.nextInt(999999); // 랜덤 난수 설정

        String setFrom = "ho78901@naver.com"; // 보내는 사람
        String toMail = userEntity.getEmail(); // 받는 사람
        String title = "[Todo] 비밀번호 변경 인증 메일입니다";
        String content = System.getProperty("line.separator") + "안녕하세요." + System.getProperty("line.separator")
                + "비밀번호 찾기 인증번호는 " + num + " 입니다." + System.getProperty("line.separator");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");

            messageHelper.setFrom(setFrom);
            messageHelper.setTo(toMail);
            messageHelper.setSubject(title);
            messageHelper.setText(content);

            mailSender.send(message);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
        ModelAndView mv = new ModelAndView();
        mv.setViewName("findPw");
        mv.addObject("num", num);

//        log.info(String.valueOf(mv.getModel().get("num")));

        return mv.getModel().get("num").toString();
    }
}
