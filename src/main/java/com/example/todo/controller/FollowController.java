package com.example.todo.controller;

import com.example.todo.dto.FollowDTO;
import com.example.todo.dto.SimpleAccountInfo;
import com.example.todo.dto.UserDTO;
import com.example.todo.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/follows")
public class FollowController {

    @Autowired
    FollowService followService;

    // 팔로우 & 언팔로우
    @PostMapping("")
    public ResponseEntity<String> addFollow(@RequestBody Map<String, String> requestBody, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(followService.followToggle(requestBody.get("email"), request));
    }

    // 팔로워 조회
    @GetMapping("/followers")
    public ResponseEntity<List<SimpleAccountInfo>> getFollowers(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(followService.getFollower(request));
    }

    // 팔로잉 조회
    @GetMapping("/followings")
    public ResponseEntity<List<SimpleAccountInfo>> getFolloings(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(followService.getFollowings(request));
    }
}
