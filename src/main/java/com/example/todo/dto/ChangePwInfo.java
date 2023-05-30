package com.example.todo.dto;

import lombok.Data;

@Data
public class ChangePwInfo {
    private String originalPw;
    private String newPw;
    private String newPwCheck;
}
