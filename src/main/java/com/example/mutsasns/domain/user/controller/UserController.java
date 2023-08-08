package com.example.mutsasns.domain.user.controller;

import com.example.mutsasns.domain.user.dto.RegisterDto;
import com.example.mutsasns.domain.user.dto.RequestUserUpdateDto;
import com.example.mutsasns.domain.user.dto.ResponseUserDto;
import com.example.mutsasns.domain.user.service.UserService;
import com.example.mutsasns.global.messages.ResponseDto;
import com.example.mutsasns.global.messages.SystemMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegisterDto dto) {
        userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.getInstance(SystemMessage.REGISTER_SUCCESS));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUserDto> readUserInfo(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.readUser(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDto> updateUserInfo(
            @PathVariable("userId") Long userId,
            @RequestBody RequestUserUpdateDto dto,
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
