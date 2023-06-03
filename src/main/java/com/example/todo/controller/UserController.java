package com.example.todo.controller;

import com.example.todo.dto.ChangePwInfo;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.UserEntity;
import com.example.todo.service.LoginService;
import com.example.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    // 내 정보 조회
    @GetMapping("")
    public ResponseEntity<Optional<UserEntity>> getMyInfo(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(userService.getMyInfo(request));
    }

    // 내 정보 수정
    @PostMapping("")
    public ResponseEntity<Void> updateMyInfo(@RequestBody UserDTO user, HttpServletRequest request) throws Exception {
        userService.updateMyInfo(user, request);
        return ResponseEntity.ok(null);
    }

    // 비밀번호 확인
    @PostMapping("/validatepw")
    public ResponseEntity<String> validatePw(@RequestBody Map<String, String> requestBody, HttpServletRequest request) throws Exception {
        if(loginService.validatePw(requestBody.get("originalPw"), request))
            return ResponseEntity.ok("비밀번호 확인");
        return ResponseEntity.ok("비밀번호 불일치");
    }

    // 비밀번호 변경
    @PostMapping("/changepw")
    public ResponseEntity<Void> updatePw(@RequestBody ChangePwInfo changePw, HttpServletRequest request) throws Exception {
        loginService.changePw(changePw, request);
        return ResponseEntity.ok(null);
    }

    // 회원 탈퇴
    @PostMapping("/delete")
    public ResponseEntity<Optional<UserEntity>> deactivateUser(HttpServletRequest request) throws Exception {
        userService.deactivateUser(request);
        return ResponseEntity.ok(null);
    }

}
