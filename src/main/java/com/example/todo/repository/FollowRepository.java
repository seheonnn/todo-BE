package com.example.todo.repository;


import com.example.todo.entities.FollowEntity;
import com.example.todo.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    Optional<FollowEntity> findByFromUserAndToUser(UserEntity fromUserIdx, UserEntity toUSerIdx);

}
