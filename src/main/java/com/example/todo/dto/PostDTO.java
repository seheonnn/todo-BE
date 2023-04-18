package com.example.todo.dto;

import com.example.todo.entities.PostEntity;
import com.example.todo.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long postIdx;

    private UserEntity user;

    private String title;

    private String description;

    private boolean shared;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean completed;

    private Long like_cnt;

    public PostEntity toEntity() {
        return PostEntity.builder()
                .user(user)
                .title(title)
                .description(description)
                .shared(shared)
                .startDate(startDate)
                .endDate(endDate)
                .completed(completed)
                .like_cnt(like_cnt)
                .build();
    }
}
