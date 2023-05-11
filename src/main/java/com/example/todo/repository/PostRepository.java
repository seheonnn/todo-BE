package com.example.todo.repository;


import com.example.todo.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
//    List<PostEntity> findByUser(UserEntity user);

    @Query("SELECT p FROM Post p WHERE p.user.userIdx=:userId")
    List<PostEntity> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p WHERE p.user.email=:email")
    List<PostEntity> findAllByEmail(@Param("email") String email);

    @Query("SELECT p FROM Post p WHERE p.shared=true")
    List<PostEntity> findSharedPosts();

    @Query("SELECT p.likeCnt FROM Post p WHERE p.postIdx=:postId")
    int getLikeCnt(@Param("postId") Long postId);

}
