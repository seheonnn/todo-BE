package com.example.todo.repository;


import com.example.todo.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
//    List<PostEntity> findByUser(UserEntity user);

    @Query("select p from Post p where p.user.userIdx=:userId")
    List<PostEntity> findAllByUserId(@Param("userId") Long userId);

    @Query("select p from Post p where p.shared=true")
    List<PostEntity> findSharedPosts();

    @Query("select p.likeCnt from Post p where p.postIdx=:postId")
    int getLikeCnt(@Param("postId") Long postId);
}
