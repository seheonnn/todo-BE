package com.example.todo.service;

import com.example.todo.dto.FollowDTO;
import com.example.todo.entities.FollowEntity;
import com.example.todo.entities.UserEntity;
import com.example.todo.repository.FollowRepository;
import com.example.todo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> followToggle(FollowDTO follow) throws Exception {
        if (follow.getFromUser().equals(follow.getToUser()))
            throw new Exception("같은 유저는 팔로우할 수 없습니다.");
        UserEntity fromUser = userRepository.findById(follow.getFromUser().getUserIdx())
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다"));
        UserEntity toUser = userRepository.findById(follow.getToUser().getUserIdx())
                .orElseThrow(() -> new Exception("대상 사용자를 찾을 수 없습니다"));

        Optional<FollowEntity> followRelation = followRepository.findByFromUserAndToUser(fromUser, toUser);


        if (followRelation.isPresent()) {
            followRepository.delete(followRelation.get());
            return ResponseEntity.ok("언팔로우 성공");
        }
        else {
            followRepository.saveAndFlush(new FollowEntity(fromUser, toUser));
            return ResponseEntity.ok("팔로우 성공");
        }
    }

}
