package com.example.todo.controller;

import com.example.todo.dto.PostDTO;
import com.example.todo.entities.PostEntity;
import com.example.todo.response.ApiResult;
import com.example.todo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/posts")
public class PostController {

    private final PostService postService;

    // 내 게시글 모두 조회
    @GetMapping(path = "")
    public ApiResult<?> posts(HttpServletRequest request, Pageable pageable) throws Exception {
        List<PostEntity> postsById = postService.findAllByUserId(request, pageable);
        if(postsById.size() == 0)
            return ApiResult.ERROR("작성된 글이 없습니다.", HttpStatus.BAD_REQUEST);
        return ApiResult.OK(postsById);
    }

    // 게시글 생성
    @PostMapping(path = "/create")
    public ApiResult<?> create(@RequestBody PostDTO postDTO, HttpServletRequest request) throws Exception {
        PostDTO post = postService.save(postDTO, request);
        if(post == null) {
            ApiResult.ERROR("글 작성 실패", HttpStatus.BAD_REQUEST);
        }
        return ApiResult.OK("글 작성 성공");
    }

    // 게시글 수정
    @PostMapping(path = "/update")
    public ApiResult<?> update(@RequestBody PostDTO postDTO) throws Exception {
        PostDTO post = postService.update(postDTO);
        return ApiResult.OK(post);
    }

    // 게시글 삭제
    @PostMapping(path = "/deleteAll")
    public ApiResult<?> deleteAll(HttpServletRequest request, Pageable pageable) throws Exception {
        List<PostEntity> postsById = postService.findAllByUserId(request, pageable);
        for (PostEntity postEntity : postsById) {
            postService.delete(postEntity.getPostIdx());
        }
        if(postsById.size() > 0) {
            ApiResult.ERROR("글 모두 삭제 실패", HttpStatus.BAD_REQUEST);
        }
        return ApiResult.OK("글 모두 삭제 성공");
    }

    //
    @PostMapping(path = "/delete/{postId}")
    public ApiResult<?> delete(@PathVariable Long postId) {
        PostEntity post = postService.findOneByPostIdx(postId);
        postService.delete(post.getPostIdx());
        if(postService.findOneByPostIdx(postId) != null) {
            ApiResult.ERROR("글 삭제 실패", HttpStatus.BAD_REQUEST);

        }
        return ApiResult.OK("글 삭제 성공");
    }

    // 공유된 게시글 조회
    @GetMapping(path = "/shared")
    public ApiResult<?> getSharedPosts() {
        return ApiResult.OK(postService.findSharedPosts());
    }

    // 좋아요 조회
    @GetMapping(path = "/like/{postId}")
    public ApiResult<?> getLikeCnt(@PathVariable Long postId) {
        return ApiResult.OK(postService.getLikeCnt(postId));
    }

    // 좋아요 / 취소
    @PostMapping(path = "/add/like")
    public ApiResult<?> addLike(@RequestBody PostDTO post, HttpServletRequest request) throws Exception {
        return ApiResult.OK(postService.addLike(post, request));
    }

    // 게시글 검색
    @GetMapping("/search")
    public ApiResult<?> posts(@RequestBody Map<String, String> requestBody, HttpServletRequest request) throws Exception {
        return ApiResult.OK(postService.searchPost(requestBody.get("keyword"), request));
    }
}
