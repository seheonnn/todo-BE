package com.example.todo.entities;
import com.example.todo.dto.UserDTO;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

import java.time.LocalDateTime;


@Entity(name = "User")
@Table(name = "User")
//@SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// , generator = "user_sequence"
    private Long userIdx;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profileImage", nullable = true)
    private String profileImage;

    @Column(name = "status", nullable = false)
    @ColumnDefault("'A'") // A: 활성 유저 D: 탈퇴 유저
    private char status;

    @Column(name = "role", nullable = false) // User, Guest
    private String role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updated_at;

    @Column(name = "login_at", nullable = true)
    private LocalDateTime login_at;

    @Column(name = "login_cnt", nullable = true)
    @ColumnDefault("0")
    private Long login_cnt;

    @PrePersist
    public void create_at(){
        this.created_at = LocalDateTime.now();
    }

    public UserDTO toDTO() {
        return UserDTO.builder()
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