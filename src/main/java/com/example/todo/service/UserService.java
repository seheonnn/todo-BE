package com.example.todo.service;

import com.example.todo.config.security.JwtTokenProvider;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;



    public Optional<UserEntity> getMyInfo(HttpServletRequest request) throws Exception {
        return userRepository.findById(jwtTokenProvider.getCurrentUser(request));
    }

    public Optional<UserEntity> updateMyInfo(UserDTO user) {
        UserEntity userEntity = userRepository.findById(user.getUserIdx()).orElse(null);
        if (userEntity == null) {
            return Optional.empty();
        }
        if (user.getName() != null) {
            userEntity.setName(user.getName());
        }
        if (user.getProfileImage() != null) {
            userEntity.setProfileImage(user.getProfileImage());
        }
        return Optional.of(userRepository.saveAndFlush(userEntity));

    }

    public Optional<UserEntity> deactivateUser(UserDTO user) {
        UserEntity userEntity = userRepository.findById(user.getUserIdx()).orElse(null);
        if (userEntity == null) {
            return Optional.empty();
        }
        userEntity.setStatus('D');
        return Optional.of(userRepository.saveAndFlush(userEntity));
    }
}
