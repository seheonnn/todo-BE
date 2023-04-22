package com.example.todo.controller;

import com.example.todo.dto.FollowDTO;
import com.example.todo.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/follows")
public class FollowController {

    @Autowired
    FollowService followService;

    @PostMapping("")
    public ResponseEntity<Void> addFollow(@RequestBody FollowDTO follow) throws Exception {
        followService.followToggle(follow);
        return ResponseEntity.ok(null);
    }
}
