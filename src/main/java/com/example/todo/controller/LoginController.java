package com.example.todo.controller;

import com.example.todo.config.ErrorResponse;
import com.example.todo.config.security.JwtTokenProvider;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "")
public class LoginController {

    @Autowired
    LoginService loginService;

    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody UserDTO user) throws Exception {
        loginService.join(user);
        return ResponseEntity.ok(null);
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody UserDTO user) throws Exception {
        UserEntity userEntity = loginService.checkUserInfo(user);
        return jwtTokenProvider.createToken(userEntity.getEmail(), userEntity.getRole());
    }
}
