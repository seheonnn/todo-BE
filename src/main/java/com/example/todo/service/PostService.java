package com.example.todo.service;

import com.example.todo.dto.PostDTO;
import com.example.todo.entities.PostEntity;
import com.example.todo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    // todo 유효성 추가

    private final PostRepository postRepository;

    public List<PostEntity> findAllByUserId(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    @Transactional
    public void save(PostDTO post) {
        PostEntity postEntity = post.toEntity();
        PostEntity newPost = PostEntity.builder()
                .user(postEntity.getUser())
                .postIdx(postEntity.getPostIdx())
                .description(postEntity.getDescription())
                .shared(false)
                .endDate(postEntity.getEndDate())
                .likeCnt(0L)
                .startDate(LocalDateTime.now())
                .title(postEntity.getTitle())
                .completed(false)
                .build();
        postRepository.save(newPost);
    }

    public void update(PostDTO post) {
        // 수정 항목 : description, shared, likeCnt, title, completed
        PostEntity postEntity = post.toEntity();
        PostEntity findPost = postRepository.findById(postEntity.getPostIdx()).get();
        findPost.setDescription(postEntity.getDescription());
        findPost.setShared(postEntity.isShared());
        findPost.setLikeCnt(postEntity.getLikeCnt());
        findPost.setCompleted(postEntity.isCompleted());
        postRepository.save(findPost);
    }

    @Transactional
    public void delete(Long postId) {
        postRepository.deleteById(postId);
    }

    public List<PostEntity> findSharedPosts() {
        return postRepository.findSharedPosts();
    }

    public int getLikeCnt(Long postId) {
        return postRepository.getLikeCnt(postId);
    }
}
