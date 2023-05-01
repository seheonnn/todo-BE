package com.example.todo.dto;

import com.example.todo.entities.LikeEntity;
import com.example.todo.entities.PostEntity;
import com.example.todo.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDTO {
    private Long likeIdx;

    private PostEntity postEntity;

    private UserEntity userEntity;

    public LikeEntity toEntity() {
        return LikeEntity.builder()
                .post(postEntity)
                .user(userEntity)
                .build();
    }
}
