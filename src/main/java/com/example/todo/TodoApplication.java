package com.example.todo;

import com.example.todo.entities.FollowEntity;
import com.example.todo.entities.PostEntity;
import com.example.todo.entities.UserEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
//@EnableJpaRepositories(basePackageClasses = {UserEntity.class, PostEntity.class, FollowEntity.class})
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

}
