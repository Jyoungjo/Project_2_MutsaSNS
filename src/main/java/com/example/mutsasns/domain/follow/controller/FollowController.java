package com.example.mutsasns.domain.follow.controller;

import com.example.mutsasns.domain.follow.dto.ResponseFollowDto;
import com.example.mutsasns.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<ResponseFollowDto> createFollow(
            @PathVariable("userId") Long userId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(followService.doFollow(userId, username));
    }
}
