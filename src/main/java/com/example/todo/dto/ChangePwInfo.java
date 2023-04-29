package com.example.todo.dto;

import lombok.Data;

@Data
public class ChangePwInfo {
    private Long userIdx;
    private String originalPw;
    private String newPw;
    private String newPwCheck;
}
