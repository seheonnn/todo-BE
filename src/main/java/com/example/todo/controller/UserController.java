package com.example.todo.controller;

import com.example.todo.dto.ChangePwInfo;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.service.LoginService;
import com.example.todo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    LoginService loginService;

    // 내 정보 조회
    @GetMapping("")
    public ResponseEntity<Optional<UserEntity>> getMyInfo(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.getMyInfo(user.getUserIdx()));
    }

    // 내 정보 수정
    @PostMapping("")
    public ResponseEntity<Void> updateMyInfo(@RequestBody UserDTO user) {
        userService.updateMyInfo(user);
        return ResponseEntity.ok(null);
    }

    // 비밀번호 변경
    @PostMapping("/changepw")
    public ResponseEntity<Void> updatePW(@RequestBody ChangePwInfo changePw) throws Exception {
        if (loginService.validatePw(changePw))
            loginService.changePw(changePw);
        return ResponseEntity.ok(null);
    }

    // 회원 탈퇴
    @PostMapping("/delete")
    public ResponseEntity<Optional<UserEntity>> deactivateUser(@RequestBody UserDTO user) {
        userService.deactivateUser(user);
        return ResponseEntity.ok(null);
    }

}
