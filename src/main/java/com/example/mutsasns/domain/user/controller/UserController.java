package com.example.mutsasns.domain.user.controller;

import com.example.mutsasns.domain.user.dto.CustomUserDetails;
import com.example.mutsasns.domain.user.dto.RegisterDto;
import com.example.mutsasns.domain.user.dto.UserUpdateRequestDto;
import com.example.mutsasns.domain.user.service.UserService;
import com.example.mutsasns.global.exception.CustomException;
import com.example.mutsasns.global.exception.ErrorCode;
import com.example.mutsasns.global.messages.ResponseDto;
import com.example.mutsasns.global.messages.SystemMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegisterDto dto) {
        // TODO 비밀번호 중복 여부와 계정 중복 등록 분리
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            log.error("입력 비밀번호 불일치");
            throw new CustomException(ErrorCode.USER_INCONSISTENT_PASSWORD);
        }
        manager.createUser(CustomUserDetails.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build());

        log.info("회원가입 성공");
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.getInstance(SystemMessage.REGISTER_SUCCESS));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDto> updateUserInfo(
            @PathVariable("userId") Long userId,
            @RequestBody UserUpdateRequestDto dto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        userService.updateUserInfo(userId, dto, username);
        return ResponseEntity.ok(ResponseDto.getInstance(SystemMessage.CHANGE_USER_INFO));
    }

    @PutMapping(value = "/{userId}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> uploadProfileImg(
            @PathVariable("userId") Long userId,
            @RequestParam("image") MultipartFile profileImg,
            Authentication authentication
    ) {
        String username = authentication.getName();
        userService.updateImg(userId, profileImg, username);
        return ResponseEntity.ok(ResponseDto.getInstance(SystemMessage.REGISTER_USER_PROFILE));
    }
}
