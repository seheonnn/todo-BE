package com.example.todo.service;

import com.example.todo.dto.PostDTO;
import com.example.todo.entities.PostEntity;
import com.example.todo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    // todo 유효성 추가

    private final PostRepository postRepository;

    public void findAllByUserId(Long userId) {
        // id가 존재하는 아이디인지 확인
        postRepository.findAllByUserId(userId);
    }

    @Transactional
    public void save(PostDTO post) {
        PostEntity postEntity = post.toEntity();
        PostEntity newPost = PostEntity.builder()
                .user(postEntity.getUser())
                .postIdx(postEntity.getPostIdx())
                .description(postEntity.getDescription())
                .shared(false)
                .endDate(null)
                .likeCnt(0L)
                .startDate(LocalDateTime.now())
                .title(postEntity.getTitle())
                .completed(false)
                .build();
        postRepository.save(newPost);
    }

    @Transactional
    public void delete(Long postId) {
        postRepository.deleteById(postId);
    }

    public void findSharedPosts() {
        postRepository.findSharedPosts();
    }

    public void getLikeCnt(Long postId) {
        postRepository.getLikeCnt(postId);
    }
}
