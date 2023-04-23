package com.example.todo.controller;

import com.example.todo.config.ErrorResponse;
import com.example.todo.dto.PostDTO;
import com.example.todo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/posts")
public class PostController {

    private final PostService postService;

    // 내 게시글 모두 조회
    @GetMapping(path = "/{userId}")
    public ResponseEntity<ErrorResponse> posts(@PathVariable Long userId) {
        try {
            // userId로 조회
            postService.findAllByUserId(userId);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 게시글 생성
    @PostMapping(path = "/write")
    public ResponseEntity<ErrorResponse> write(@RequestBody PostDTO post) {
        try {
            postService.save(post);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 게시글 수정
    @PostMapping(path = "/update")
    public ResponseEntity<ErrorResponse> update(@RequestBody PostDTO post) {
        try {
            postService.save(post);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 게시글 삭제
    @PostMapping(path = "/delete")
    public ResponseEntity<ErrorResponse> delete(@RequestBody PostDTO post) {
        try {
            postService.delete(post.toEntity().getPostIdx());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 공유된 게시글 조회
    @PostMapping(path = "/posts/shared")
    public ResponseEntity<ErrorResponse> getSharedPosts() {
        try {
            postService.findSharedPosts();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 좋아요 조회
    @GetMapping(path = "/posts/like/{postId}")
    public ResponseEntity<ErrorResponse> getLikeCnt(@PathVariable Long postId) {
        try {
            postService.getLikeCnt(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }


}
