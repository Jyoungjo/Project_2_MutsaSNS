package com.example.mutsasns.global.filter;

import com.example.mutsasns.domain.user.dto.CustomUserDetails;
import com.example.mutsasns.domain.user.dto.LoginDto;
import com.example.mutsasns.global.exception.ErrorCode;
import com.example.mutsasns.global.jwt.provider.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
    1. 로그인 요청 URI 정의
    2. JSON 형식의 데이터를 변환
    3. 생성된 UsernamePasswordAuthenticationToken(Authentication 객체) 를 가지고 Authentication 에게 인증 위임
    4. 로그인 요청으로 받은 정보들을 바탕으로 UsernamePasswordAuthenticationToken 생성
    5. 로그인 성공 시 토큰 생성
 */

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/user/login"); // 1
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class); // 2

            return getAuthenticationManager().authenticate( // 3
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword(), null) // 4
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        log.info("로그인 성공");
        String username = ((CustomUserDetails) authResult.getPrincipal()).getUsername();

        String token = jwtUtil.generateToken(username); // 5
//        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token); -> 헤더에 토큰 전달 시 작성해야 하는 코드
        setTokenToResponse(response, token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.error("로그인 실패");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("errorCode", ErrorCode.USER_INCONSISTENT_USERNAME_PASSWORD.name());
        result.put("message", ErrorCode.USER_INCONSISTENT_USERNAME_PASSWORD.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    private void setTokenToResponse(HttpServletResponse response, String token) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
