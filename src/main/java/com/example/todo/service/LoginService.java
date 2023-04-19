package com.example.todo.service;

import com.example.todo.config.RoleType;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@Slf4j
public class LoginService {

    @Autowired
    private UserRepository userRepository;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
                .created_at(new Timestamp(System.currentTimeMillis()).toLocalDateTime())
                .build();
        return userRepository.saveAndFlush(newUser);
    }

    public UserEntity checkUserInfo(UserDTO user) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        if (!encoder.matches(user.getPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return userEntity;
    }
}
