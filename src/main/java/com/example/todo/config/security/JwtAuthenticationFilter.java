package com.example.todo.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Header 에서 JWT 를 받아옴.
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        // 유효한 token 인지 확인.
//        log.info(jwtTokenProvider.validateToken(token));
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // token 이 유효하면 해당 token 으로부터 user 정보를 받아옴.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // SecurityContext 에 Authentication 객체를 저장함.
            SecurityContextHolder.getContext().setAuthentication(authentication);
//            log.info(SecurityContextHolder.getContext());
        }
        chain.doFilter(request, response);
    }
}
