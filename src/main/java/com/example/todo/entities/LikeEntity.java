package com.example.todo.entities;

import com.example.todo.dto.LikeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity(name = "Like")
@Table(name = "Likes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeIdx;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "postIdx")
    private PostEntity post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userIdx")
    private UserEntity user;

    public LikeEntity(PostEntity post, UserEntity user) {
        this.post = post;
        this.user = user;
    }

    public LikeDTO toDTO() {
        return LikeDTO.builder()
                .postEntity(post)
                .userEntity(user)
                .build();
    }
}
