package com.example.mutsasns.global.filter;

import com.example.mutsasns.global.exception.CustomException;
import com.example.mutsasns.global.jwt.provider.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsManager manager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = jwtUtil.getJwtFromHeader(request);

        try {
            if (StringUtils.hasText(tokenValue)) {
                if (!jwtUtil.validate(tokenValue)) {
                    log.error("validate fail");
                    return;
                }

                Claims claims = jwtUtil.parseClaims(tokenValue);

                setAuthentication(claims.getSubject());
            }
        } catch (CustomException e) {
            // TODO null 왜 뜨는지 공부해보기
            log.warn(e.getMessage());
//            request.setAttribute("exception", e.getErrorCode());
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String username) {
        UserDetails userDetails = manager.loadUserByUsername(username);
        // TODO ROLE, AUTHORITY 설정 해주기
        return new UsernamePasswordAuthenticationToken(userDetails, null, null);
    }
}
