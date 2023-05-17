package com.example.todo.service;

import com.example.todo.config.security.JwtTokenProvider;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTemplate redisTemplate;


    public Optional<UserEntity> getMyInfo(HttpServletRequest request) throws Exception {
        return userRepository.findByEmail(jwtTokenProvider.getCurrentUser(request));
    }

    public Optional<UserEntity> updateMyInfo(UserDTO user, HttpServletRequest request) throws Exception {
        String email = jwtTokenProvider.getCurrentUser(request);
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        if (userEntity == null) {
            throw new Exception("사용자를 찾을 수 없습니다.");
        }
        if (user.getName() != null) {
            userEntity.setName(user.getName());
        }
        if (user.getProfileImage() != null) {
            userEntity.setProfileImage(user.getProfileImage());
        }
        return Optional.of(userRepository.saveAndFlush(userEntity));

    }

    public Optional<UserEntity> deactivateUser(HttpServletRequest request) throws Exception {
        String email = jwtTokenProvider.getCurrentUser(request);
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        if (userEntity == null) {
            throw new Exception("사용자를 찾을 수 없습니다.");
        }

        // Redis 에서 로그인되어 있는 토큰 삭제
        Object token = redisTemplate.opsForValue().get("RT:" + email);
        if (token != null) {
            redisTemplate.delete("RT:"+email);
        }
        // 탈퇴한 토큰 차단
        Long expire = jwtTokenProvider.getExpireTime((String) token).getTime();
        redisTemplate.opsForValue().set(token, "logout", expire, TimeUnit.MILLISECONDS);

        userEntity.setStatus('D');
        return Optional.of(userRepository.saveAndFlush(userEntity));
    }
}
