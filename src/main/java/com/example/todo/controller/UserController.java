package com.example.todo.controller;

import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.service.UserService;
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

    // 내 정보 조회
    @GetMapping("")
    public ResponseEntity<Optional<UserEntity>> getMyInfo(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.getMyInfo(user.getUserIdx()));

    }

    // 내 정보 수정
    @PostMapping("")
    public ResponseEntity<Optional<UserEntity>> updateMyInfo(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.updateMyInfo(user));
    }
}
