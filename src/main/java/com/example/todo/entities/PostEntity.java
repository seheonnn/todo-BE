package com.example.todo.entities;

import com.example.todo.dto.PostDTO;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "Post")
@Table(name = "Post")
//@SequenceGenerator(name = "post_sequence", sequenceName = "post_sequence", allocationSize = 1)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postIdx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userIdx", referencedColumnName = "userIdx")
    private UserEntity user;


    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "shared", nullable = false)
    private boolean shared;

    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "completed", nullable = false)
    @ColumnDefault("false")
    private boolean completed;


    @Column(name = "like_cnt", nullable = false)
    @ColumnDefault("0")
    private Long like_cnt;

    public PostDTO toDTO() {
        return PostDTO.builder()
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
