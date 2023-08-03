package com.example.mutsasns.global.jwt.provider;

import com.example.mutsasns.global.exception.CustomException;
import com.example.mutsasns.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final Key key;
    private final JwtParser jwtParser;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    public boolean validate(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            log.info("validate success");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("expired jwt presented");
            throw new CustomException(ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            log.warn("unsupported jwt");
            throw new CustomException(ErrorCode.UNSUPPORTED_JWT);
        } catch (SignatureException | MalformedJwtException e) {
            log.warn("malformed jwt");
            throw new CustomException(ErrorCode.INVALID_JWT);
        } catch (IllegalArgumentException e) {
            log.warn("illegal argument");
            throw new CustomException(ErrorCode.ILLEGAL_ARGUMENT_JWT);
        }
    }

    public Claims parseClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public String generateToken(String username) {
        Claims jwtClaims = Jwts.claims()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));

        return BEARER_PREFIX + Jwts.builder().setClaims(jwtClaims).signWith(key).compact();
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
