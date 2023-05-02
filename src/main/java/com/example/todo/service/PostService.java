package com.example.todo.service;

import com.example.todo.dto.PostDTO;
import com.example.todo.entities.LikeEntity;
import com.example.todo.entities.PostEntity;
import com.example.todo.entities.UserEntity;
import com.example.todo.repository.FollowRepository;
import com.example.todo.repository.LikeRepository;
import com.example.todo.repository.PostRepository;
import com.example.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    // todo 유효성 추가

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;

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

    @Transactional
    public ResponseEntity<String> addLike(
            Long userId,
            PostDTO post) throws Exception {
        UserEntity writerEntity = userRepository.findById(post.getUser().getUserIdx())
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다"));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다"));
        PostEntity postEntity = post.toEntity();

        if(writerEntity.getUserIdx().equals(userEntity.getUserIdx())) {
            throw new Exception("같은 유저는 좋아요를 할 수 없습니다.");
        }

        // 친구 관계인지 확인
        if(!followRepository.findAllByToUser(userEntity.getUserIdx()).contains(writerEntity)
                && !followRepository.findAllByFromUser(userEntity.getUserIdx()).contains(writerEntity)) {
            throw new Exception("친구 관계가 아니므로 좋아요를 할 수 없습니다.");
        }

        Optional<LikeEntity> findLikeEntity = likeRepository.findByPostAndUser(postEntity, userEntity);
        if(findLikeEntity.isPresent()) {
            likeRepository.delete(findLikeEntity.get());
            postEntity.setLikeCnt(postEntity.getLikeCnt()-1);
            return ResponseEntity.ok("좋아요 취소 성공");
        } else {
            likeRepository.saveAndFlush(new LikeEntity(postEntity, userEntity));
            postEntity.setLikeCnt(postEntity.getLikeCnt()+1);
            return ResponseEntity.ok("좋아요 성공");
        }
    }
}
