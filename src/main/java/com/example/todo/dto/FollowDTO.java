package com.example.todo.dto;

import com.example.todo.entities.FollowEntity;
import com.example.todo.entities.UserEntity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowDTO {
    private Long followIdx;

    private UserEntity fromUser;

    private UserEntity toUser;

    public FollowEntity toEntity() {
        return FollowEntity.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
    }
}
