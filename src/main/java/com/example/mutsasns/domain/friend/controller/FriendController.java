package com.example.mutsasns.domain.friend.controller;

import com.example.mutsasns.domain.friend.dto.RequestFriendUpdateDto;
import com.example.mutsasns.domain.friend.dto.ResponseFriendDto;
import com.example.mutsasns.domain.friend.service.FriendService;
import com.example.mutsasns.global.messages.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.mutsasns.global.messages.SystemMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/friends")
public class FriendController {
    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<ResponseDto> requestFriend(
            @PathVariable("userId") Long userId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        friendService.requestFriend(username, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.getInstance(REGISTER_FRIEND_REQUEST));
    }

    @GetMapping
    public ResponseEntity<List<ResponseFriendDto>> readAllRequestByResponseUser(
            @PathVariable("userId") Long userId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(friendService.readAllRequest(userId, username));
    }

    @PutMapping("/{friendId}")
    public ResponseEntity<ResponseDto> updateRequestStatus(
            @PathVariable("userId") Long userId,
            @PathVariable("friendId") Long friendId,
            @RequestBody RequestFriendUpdateDto dto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        friendService.updateStatus(username, userId, friendId, dto);
        return ResponseEntity.ok(ResponseDto.getInstance(UPDATE_FRIEND_REQUEST));
    }
}
