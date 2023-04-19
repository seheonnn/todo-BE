package com.example.todo.dto;

import com.example.todo.entities.UserEntity;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@ToString
@Builder
public class UserDTO {
    private Long userIdx;

    private String email;

    private String password;

    private String name;

    private String profileImage;

    private char status;

    private String role;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    private LocalDateTime login_at;

    private Long login_cnt;

    public UserEntity toEntity() {
        return UserEntity.builder()
                .email(email)
                .password(password)
                .name(name)
                .profileImage(profileImage)
                .status(status)
                .role(role)
                .created_at(created_at)
                .updated_at(updated_at)
                .login_at(login_at)
                .login_cnt(login_cnt)
                .build();
    }
}
