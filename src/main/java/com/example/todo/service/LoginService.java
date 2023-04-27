package com.example.todo.service;

import com.example.todo.config.RoleType;
import com.example.todo.config.security.CustomUserDetailService;
import com.example.todo.config.security.JwtTokenProvider;
import com.example.todo.dto.TokenDTO;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
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
    private CustomUserDetailService customUserDetailService;

    @Autowired
    JavaMailSender mailSender;
    private String secretKey;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;


    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();



    @Autowired
    public void JwtProvider(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public UserEntity getCurrentUser(HttpServletRequest request) throws Exception {
        // Authorization 헤더에서 JWT 토큰 추출
        String jwtToken = jwtTokenProvider.resolveToken(request);
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new Exception("유효하지 않은 토큰입니다.");
        }
//        log.info(jwtToken);
        // JWT 토큰에서 사용자 정보 추출
        String username = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();

        // 추출한 사용자 정보를 이용하여 UserDetails 객체 반환
//        log.info(String.valueOf(customUserDetailService.loadUserByUsername(username)));
        return (UserEntity) customUserDetailService.loadUserByUsername(username);
    }

    public UserEntity join(UserDTO user) throws Exception {
        UserEntity userEntity = user.toEntity();
        if(userRepository.findByEmail(userEntity.getEmail()).isPresent()){
            throw new Exception("이미 존재하는 이메일입니다.");
        }
        String encryptedPw = encoder.encode(userEntity.getPassword());
        UserEntity newUser = UserEntity.builder()
                .email(userEntity.getEmail())
                .password(encryptedPw)
                .name(userEntity.getName())
                .profileImage(userEntity.getProfileImage())
                .status('A')
                .role(String.valueOf(RoleType.USER))
                .login_cnt(0L)
                .created_at(new Timestamp(System.currentTimeMillis()).toLocalDateTime())
                .build();
        return userRepository.saveAndFlush(newUser);
    }

    public String login(UserDTO user) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new Exception("가입되지 않은 이메일입니다."));
        if (!encoder.matches(user.getPassword(), userEntity.getPassword())) {
            throw new Exception("잘못된 비밀번호입니다.");
        }
        userEntity.setLogin_at(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        userEntity.setLogin_cnt(userEntity.getLogin_cnt()+1);
        userRepository.saveAndFlush(userEntity);

        // token 발급
        TokenDTO token = jwtTokenProvider.createToken(userEntity.getEmail(), userEntity.getRole());

        // Redis 에 RTL user@email.com(key) : ----token-----(value) 형태로 token 저장
        redisTemplate.opsForValue().set("RT:"+userEntity.getEmail(), token.getRefreshToken(), token.getRefreshTokenExpiresTime().getTime(), TimeUnit.MILLISECONDS);
        return token.getRefreshToken();
    }

    public String logout(HttpServletRequest request) {
        try {
            // token 으로 user 정보 받음
            UserEntity user = getCurrentUser(request);

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

    public boolean validatePw(UserDTO user, String originalPw) throws Exception {
        UserEntity userEntity = userRepository.findById(user.getUserIdx()).orElse(null);
        if (encoder.matches(originalPw, userEntity.getPassword()))
            return true;
        else throw new Exception("비밀번호 불일치");
    }

    public Optional<UserEntity> changePw(UserDTO user, String newPw, String newPwCheck) throws Exception {
        UserEntity userEntity = userRepository.findById(user.getUserIdx()).orElse(null);
        String encryptedPw = encoder.encode(newPw);
        if (newPw.equals(newPwCheck)) {
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

        log.info(String.valueOf(mv.getModel().get("num")));

        return mv.getModel().get("num").toString();
    }
}
