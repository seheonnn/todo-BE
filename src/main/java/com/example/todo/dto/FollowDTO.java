package com.example.todo.dto;

import com.example.todo.entities.FollowEntity;
import com.example.todo.entities.UserEntity;
import lombok.*;

//@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowDTO {
    private Long followIdx;

    private UserEntity toUser;

    private UserEntity fromUser;

    public FollowEntity toEntity() {
        return FollowEntity.builder()
                .toUser(toUser)
                .fromUser(fromUser)
                .build();
    }
}
