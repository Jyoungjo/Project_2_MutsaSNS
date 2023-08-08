package com.example.mutsasns.domain.friend.service;

import com.example.mutsasns.domain.friend.domain.Friend;
import com.example.mutsasns.domain.friend.dto.RequestFriendUpdateDto;
import com.example.mutsasns.domain.friend.dto.ResponseFriendDto;
import com.example.mutsasns.domain.friend.repository.FriendRepository;
import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.repository.UserRepository;
import com.example.mutsasns.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.mutsasns.domain.user.domain.RequestStatus.*;
import static com.example.mutsasns.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // 친구 신청
    @Transactional
    public void requestFriend(String username, Long userId) {
        User reqUser = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        User resUser = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (reqUser.equals(resUser)) throw new CustomException(FRIEND_REQUEST_NOT_YOUR_PROFILE);

        if (friendRepository.existsByRequestUserAndResponseUserAndStatus(reqUser, resUser, PENDING)) {
            throw new CustomException(FRIEND_ALREADY_REQUEST);
        }

        Friend friend = new Friend(reqUser, resUser);

        friendRepository.save(friend);
        log.info("친구 신청 성공");
    }

    // 신청 목록 조회 - 신청을 받은 사람만 조회하도록
    public List<ResponseFriendDto> readAllRequest(Long userId, String username) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if (!user.getUsername().equals(username)) throw new CustomException(AUTHORITY_FORBIDDEN);

        List<Friend> friendList = friendRepository.findAllByResponseUserAndStatus(user, PENDING);

        log.info("친구 신청 목록 조회 성공");
        return friendList.stream().map(ResponseFriendDto::fromEntity).toList();
    }

    // 신청 사항 수락 or 거절
    @Transactional
    public void updateStatus(String username, Long userId, Long friendId, RequestFriendUpdateDto dto) {
        User resUser = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(FRIEND_REQUEST_NOT_FOUND));

        checkAuthority(resUser, username, friend);

        if (friend.getStatus().equals(PENDING)) {
            if (dto.getStatus().equals(ACCEPT)) {
                friend.acceptRequest(dto.getStatus());
                friendRepository.save(friend);
            } else {
                friend.rejectRequest(dto.getStatus());
                friendRepository.save(friend);
            }
        } else throw new CustomException(FRIEND_REQUEST_ALREADY_PROCESSED);

        log.info("친구 신청 상태 변경 성공");
    }

    private void checkAuthority(User resUser, String username, Friend friend) {
        if (!resUser.getUsername().equals(username) || !friend.getResponseUser().equals(resUser)) {
            throw new CustomException(AUTHORITY_FORBIDDEN);
        }
    }
}
