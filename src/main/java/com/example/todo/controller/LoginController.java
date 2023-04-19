package com.example.todo.controller;

import com.example.todo.config.ErrorResponse;
import com.example.todo.dto.UserDTO;
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
    @PostMapping("/join")
    public ResponseEntity<ErrorResponse> join (@RequestBody UserDTO user) {
        try {
            loginService.join(user);
            return ResponseEntity.ok().build();

        } catch (Exception exception) {
            new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
        }
    }
}
