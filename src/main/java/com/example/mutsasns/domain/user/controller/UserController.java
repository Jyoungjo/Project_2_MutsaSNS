package com.example.mutsasns.domain.user.controller;

import com.example.mutsasns.domain.user.dto.CustomUserDetails;
import com.example.mutsasns.domain.user.dto.RegisterDto;
import com.example.mutsasns.domain.user.exception.NotMatchPasswordException;
import com.example.mutsasns.global.messages.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/user/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegisterDto dto) {
        // TODO 비밀번호 중복 여부와 계정 중복 등록 분리
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            log.error("입력 비밀번호 불일치");
            throw new NotMatchPasswordException();
        }
        manager.createUser(CustomUserDetails.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build());

        log.info("회원가입 성공");
        // TODO System Message 생성
        return ResponseEntity.ok(ResponseDto.getInstance("회원가입이 완료되었습니다."));
    }
}
