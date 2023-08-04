package com.example.mutsasns.domain.user.controller;

import com.example.mutsasns.domain.user.dto.RegisterDto;
import com.example.mutsasns.domain.user.dto.UserUpdateRequestDto;
import com.example.mutsasns.domain.user.service.UserService;
import com.example.mutsasns.global.messages.ResponseDto;
import com.example.mutsasns.global.messages.SystemMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegisterDto dto) {
        userService.registerUser(dto);
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
        log.info("정보 변경 성공");
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
        log.info("이미지 등록 성공");
        return ResponseEntity.ok(ResponseDto.getInstance(SystemMessage.REGISTER_USER_PROFILE));
    }
}
