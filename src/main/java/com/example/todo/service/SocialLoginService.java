package com.example.todo.service;

import com.example.todo.config.RoleType;
import com.example.todo.config.security.JwtTokenProvider;
import com.example.todo.dto.TokenDTO;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocialLoginService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RedisTemplate redisTemplate;
    public String socialLogin(String email, String name) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isPresent()) {

            UserEntity userEntity = optionalUserEntity.get();
            userEntity.setLogin_at(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
            userEntity.setLogin_cnt(userEntity.getLogin_cnt()+1);
            userRepository.saveAndFlush(userEntity);

            // token 발급
            TokenDTO token = jwtTokenProvider.createAccessToken(email, "USER");

            // Redis 에 RTL user@email.com(key) : ----token-----(value) 형태로 token 저장
            redisTemplate.opsForValue().set("RT:"+email, token.getToken(), token.getRefreshTokenExpiresTime().getTime(), TimeUnit.MILLISECONDS);
            return token.getToken();
        }
        else {
            UserEntity newUser = UserEntity.builder()
                    .email(email)
                    .name(name)
                    .status('A')
                    .role(String.valueOf(RoleType.USER))
                    .login_cnt(0L)
                    .created_at(new Timestamp(System.currentTimeMillis()).toLocalDateTime())
                    .build();
            return String.valueOf(userRepository.saveAndFlush(newUser));
        }
    }
}
