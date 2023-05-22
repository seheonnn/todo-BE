package com.example.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TokenDTO {

    private String types;
    private String token;
    private Date RefreshTokenExpiresTime;
}
