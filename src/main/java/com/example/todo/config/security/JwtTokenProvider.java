package com.example.todo.config.security;

import com.example.todo.dto.TokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;


@Log4j2
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 유효시간 30분
    private long tokenValidTime = 30 * 60 * 1000L;
    private final UserDetailsService userDetailsService;

    // 객체 초기화, secretKey 를 Base64 로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    // JWT 토큰 생성
    public TokenDTO createToken(String email, String roles) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(email)); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key : value 쌍으로 저장된다.
        Date now = new Date();
        Date expiresTime = new Date(now.getTime() + tokenValidTime);
//        log.info("발급 secret key: " + secretKey);
        String token = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(expiresTime) // Expire Time 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 signature 에 들어갈 secretkey 값 설정
                .compact();
        return new TokenDTO(token, expiresTime);
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request 의 Header 에서 token 값을 가졍옵니다. "X-AUTH-TOKEN" : "token--"
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception exception) {
//            log.info("검증 secret key: " + secretKey);
            return false;
        }
    }

    // 토큰 만료 시간 확인
    public Date getExpireTime(String jwtToken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
        return claims.getBody().getExpiration();
    }

    // 토큰에서 User 정보 추출
    public String getCurrentUser(HttpServletRequest request) throws Exception {
        // Authorization 헤더에서 JWT 토큰 추출
        String jwtToken = resolveToken(request);
        if (!validateToken(jwtToken)) {
            throw new Exception("유효하지 않은 토큰입니다.");
        }

        // JWT 토큰에서 사용자 정보 추출
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
    }
}
