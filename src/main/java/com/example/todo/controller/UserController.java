package com.example.todo.controller;

import com.example.todo.dto.UserDTO;
import com.example.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    UserService userService;

    // 내 정보 조회
    @GetMapping("")
    public ResponseEntity<Void> getUserInfo(@RequestBody UserDTO user) {
        userService.getUserInfo(user.getUserIdx());
        return ResponseEntity.ok(null);

    }
}
