package com.example.todo.controller;

import com.example.todo.dto.UserDTO;
import com.example.todo.entities.OAuthToken;
import com.example.todo.service.LoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "")
public class LoginController {

    @Autowired
    LoginService loginService;


    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody UserDTO user) throws Exception {
        loginService.join(user);
        return ResponseEntity.ok(null);
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody UserDTO user) throws Exception {
        return loginService.login(user);
    }

    // 로그아웃
    @PostMapping("/log-out")
    public ResponseEntity<String> logout(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(loginService.logout(request));
    }

    //비밀번호 찾기
    @PostMapping("/findpw")
    public ResponseEntity<String> findPw(@RequestBody UserDTO user) throws Exception {
        return ResponseEntity.ok(loginService.findPw(user));
    }

    // ----- 소셜 로그인 관련

    @GetMapping("/oauth2/kakao")
    public @ResponseBody String kakaoCallback(String code) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "201335fb146c62ee03014514464ab531");
        params.add("redirect_uri", "http://localhost:8080/oauth2/kakao");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        RestTemplate rt2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer "+oAuthToken.getAccess_token());
        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String,String>> kakaoProfileRequest2 = new HttpEntity<>(headers2);
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );


        return response2.getBody();
    }
}
