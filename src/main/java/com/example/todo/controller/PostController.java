package com.example.todo.controller;

import com.example.todo.config.ErrorResponse;
import com.example.todo.dto.PostDTO;
import com.example.todo.entities.PostEntity;
import com.example.todo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/posts")
public class PostController {

    private final PostService postService;

    // 내 게시글 모두 조회
    @GetMapping(path = "")
    public List<PostEntity> posts(HttpServletRequest request) throws Exception {
        return postService.findAllByUserId(request);
    }

    // 게시글 생성
    @PostMapping(path = "/create")
    public ResponseEntity<ErrorResponse> write(@RequestBody PostDTO post) {
        postService.save(post);
        return ResponseEntity.ok().build();
    }

    // 게시글 수정
    @PostMapping(path = "/update")
    public ResponseEntity<ErrorResponse> update(@RequestBody PostDTO post) {
        postService.update(post);
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @PostMapping(path = "/delete")
    public ResponseEntity<ErrorResponse> delete(@RequestBody PostDTO post) {
        postService.delete(post.toEntity().getPostIdx());
        return ResponseEntity.ok().build();
    }

    // 공유된 게시글 조회
    @GetMapping(path = "/shared")
    public List<PostEntity> getSharedPosts() {
        return postService.findSharedPosts();
    }

    // 좋아요 조회
    @GetMapping(path = "/like/{postId}")
    public int getLikeCnt(@PathVariable Long postId) {
        return postService.getLikeCnt(postId);
    }

    // 좋아요 / 취소
    @PostMapping(path = "/add/like")
    public ResponseEntity<Void> addLike(@RequestBody PostDTO post, HttpServletRequest request) throws Exception {
        postService.addLike(post, request);
        return ResponseEntity.ok(null);
    }
}
