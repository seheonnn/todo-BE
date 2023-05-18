package com.example.todo.entities;

import com.example.todo.dto.PostDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "Post")
@Table(name = "Post")
//@SequenceGenerator(name = "post_sequence", sequenceName = "post_sequence", allocationSize = 1)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postIdx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userIdx", referencedColumnName = "userIdx")
    private UserEntity user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "shared", nullable = false)
    private boolean shared;

    @Column(name = "startDate", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @Column(name = "endDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @Column(name = "completed", nullable = false)
    @ColumnDefault("false")
    private boolean completed;


    @Column(name = "like_cnt", nullable = true)
    @ColumnDefault("0")
    private Long likeCnt;

    public PostDTO toDTO() {
        return PostDTO.builder()
                .postIdx(postIdx)
                .user(user)
                .title(title)
                .description(description)
                .shared(shared)
                .startDate(startDate)
                .endDate(endDate)
                .completed(completed)
                .likeCnt(likeCnt)
                .build();
    }
}
