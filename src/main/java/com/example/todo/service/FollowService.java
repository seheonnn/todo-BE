package com.example.todo.service;

import com.example.todo.config.security.JwtTokenProvider;
import com.example.todo.dto.FollowDTO;
import com.example.todo.dto.SimpleAccountInfo;
import com.example.todo.dto.UserDTO;
import com.example.todo.entities.FollowEntity;
import com.example.todo.entities.UserEntity;
import com.example.todo.repository.FollowRepository;
import com.example.todo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FollowService {

    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public String followToggle(String toUserEmail, HttpServletRequest request) throws Exception {
        String fromUserEmail = jwtTokenProvider.getCurrentUser(request);
        if (fromUserEmail.equals(toUserEmail))
            throw new Exception("같은 유저는 팔로우할 수 없습니다.");

        log.info(toUserEmail);
        log.info(fromUserEmail);
        UserEntity fromUser = userRepository.findByEmail(fromUserEmail)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다"));
        UserEntity toUser = userRepository.findByEmail(toUserEmail)
                .orElseThrow(() -> new Exception("대상 사용자를 찾을 수 없습니다"));

        Optional<FollowEntity> followRelation = followRepository.findByFromUserAndToUser(fromUser, toUser);


        if (followRelation.isPresent()) {
            followRepository.delete(followRelation.get());
            return "언팔로우 성공";
        }
        else {
            followRepository.saveAndFlush(new FollowEntity(fromUser, toUser));
            return "팔로우 성공";
        }
    }

    public List<SimpleAccountInfo> getFollower(HttpServletRequest request) throws Exception {
        String email = jwtTokenProvider.getCurrentUser(request);
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다"));

        List<UserEntity> followers = followRepository.findAllByToUser(userEntity.getUserIdx());
        log.info(followers.toString());
        List<SimpleAccountInfo> userInfos = new ArrayList<>();
        for(UserEntity u: followers) {
            userInfos.add(new SimpleAccountInfo(u.getUserIdx(), u.getEmail(), u.getName(), u.getProfileImage()));
        }
        return userInfos;
    }

    public List<SimpleAccountInfo> getFollowings(HttpServletRequest request) throws Exception {
        String email = jwtTokenProvider.getCurrentUser(request);
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다"));

        List<UserEntity> followings = followRepository.findAllByFromUser(userEntity.getUserIdx());
        log.info(followings.toString());
        List<SimpleAccountInfo> userInfos = new ArrayList<>();
        for(UserEntity u: followings) {
            userInfos.add(new SimpleAccountInfo(u.getUserIdx(), u.getEmail(), u.getName(), u.getProfileImage()));
        }
        return userInfos;
    }

}
