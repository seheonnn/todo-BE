package com.example.todo.entities;

import com.example.todo.dto.FollowDTO;
import lombok.*;

import javax.persistence.*;

@Entity(name = "Follow")
@Table(name = "Follow")
//@SequenceGenerator(name = "follow_sequence", sequenceName = "follow_sequence", allocationSize = 1)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followIdx;

    @ManyToOne
    @JoinColumn(name = "from_user") // 팔로우 요청하는 user
    private UserEntity fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user") // 다른 사용자에 의해 팔로우 받는 user
    private UserEntity toUser;

    public FollowEntity(UserEntity fromUser, UserEntity toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public FollowDTO toDTO() {
        return FollowDTO.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
    }
}
