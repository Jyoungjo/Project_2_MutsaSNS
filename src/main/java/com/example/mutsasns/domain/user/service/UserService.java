package com.example.mutsasns.domain.user.service;

import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.dto.CustomUserDetails;
import com.example.mutsasns.domain.user.dto.RegisterDto;
import com.example.mutsasns.domain.user.dto.UserUpdateRequestDto;
import com.example.mutsasns.domain.user.repository.UserRepository;
import com.example.mutsasns.global.exception.CustomException;
import com.example.mutsasns.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void registerUser(RegisterDto dto) {
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
    }

    public void updateUserInfo(Long userId, UserUpdateRequestDto dto, String username) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        checkAccountForRegister(user, username, dto.getOldPassword());

        if (!dto.getNewPassword().equals(dto.getPasswordCheck()))
            throw new CustomException(ErrorCode.USER_INCONSISTENT_PASSWORD);

        user.updateInfo(dto);
        userRepository.save(user);
    }

    public void updateImg(Long userId, MultipartFile image, String username) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        checkAccount(user, username);

        String profileImgDir = String.format("profile/%d/", userId);
        try {
            Files.createDirectories(Path.of(profileImgDir));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String originalFilename = image.getOriginalFilename();
        String[] fileNameSplit = originalFilename.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        String profileFilename = "profile_" + userId + "." + extension;
        String profileImagePath = profileImgDir + profileFilename;

        try {
            image.transferTo(Path.of(profileImagePath));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        user.setProfileImg(String.format("/static/%d/%s", userId, profileFilename));
        userRepository.save(user);
    }

    // TODO 회원 탈퇴 기능 구현

    // 인증 메소드
    private void checkAccountForRegister(User user, String username, String oldPassword) {
        // 다른 유저의 프로필을 수정할 수도 있기 때문에
        // 현재 로그인 한 유저와 프로필 이미지를 등록할 유저의 username 비교
        if (!user.getUsername().equals(username) || !user.getPassword().equals(oldPassword)) {
            log.error("계정 확인 실패");
            throw new CustomException(ErrorCode.USER_INCONSISTENT_USERNAME_PASSWORD);
        }
    }

    private void checkAccount(User user, String username) {
        // 다른 유저의 프로필을 수정할 수도 있기 때문에
        // 현재 로그인 한 유저와 프로필 이미지를 등록할 유저의 username 비교
        if (!user.getUsername().equals(username)) {
            log.error("계정 확인 실패");
            throw new CustomException(ErrorCode.USER_INCONSISTENT_USERNAME_PASSWORD);
        }
    }
}
