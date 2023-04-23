package com.example.todo.repository;


import com.example.todo.entities.FollowEntity;
import com.example.todo.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    Optional<FollowEntity> findByFromUserAndToUser(UserEntity fromUserIdx, UserEntity toUSerIdx);

    // follower 찾기
    @Query(value = "SELECT u FROM Follow f JOIN f.fromUser u WHERE f.toUser.userIdx = :userIdx")
    List<UserEntity> findAllByToUser(@Param("userIdx") Long userIdx);

}
