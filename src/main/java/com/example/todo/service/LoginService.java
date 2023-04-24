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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailService customUserDetailService;
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
        String jwtToken = request.getHeader("X-AUTH-TOKEN");
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
//        log.info(userEntity.getPassword());
//        log.info(originalPw);
//        log.info(String.valueOf(encoder.matches(userEntity.getPassword(), originalPw)));
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
}
