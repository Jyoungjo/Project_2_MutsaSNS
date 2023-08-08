package com.example.mutsasns.domain.follow.service;

import com.example.mutsasns.domain.follow.domain.Follow;
import com.example.mutsasns.domain.follow.dto.ResponseFollowDto;
import com.example.mutsasns.domain.follow.repository.FollowRepository;
import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.repository.UserRepository;
import com.example.mutsasns.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.mutsasns.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // 팔로우 한다고 할때
    // 프론트에서 responseBody 를 어떻게 다루느냐에 따라 달라지겠지만 로직이 수행되면 데이터를 넘겨주는 방식으로 설계
    @Transactional
    public ResponseFollowDto doFollow(Long userId, String username) {
        User followUser = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        User followingUser = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (followUser.getUsername().equals(followingUser.getUsername()))
            throw new CustomException(FOLLOW_NOT_YOUR_PROFILE);

        Optional<Follow> optionalFollow = followRepository.findByFollowerAndFollowing(followUser, followingUser);

        if (optionalFollow.isPresent()) {
            unfollow(optionalFollow.get(), followUser, followingUser);

            log.info("언팔로우 : unfollower - {}, unfollowing - {}", followUser.getUsername(), followingUser.getUsername());
            return ResponseFollowDto.builder()
                    .targetUsername(followUser.getUsername())
                    .myUsername(followingUser.getUsername())
                    .followVariation(-1)
                    .followingVariation(-1)
                    .build();
        }

        Follow followEntity = Follow.builder()
                .follower(followUser)
                .following(followingUser)
                .build();

        follow(followEntity, followUser, followingUser);

        log.info("팔로우 : follower - {}, following - {}", followUser.getUsername(), followingUser.getUsername());
        return ResponseFollowDto.builder()
                .targetUsername(followUser.getUsername())
                .myUsername(followingUser.getUsername())
                .followVariation(1)
                .followingVariation(1)
                .build();
    }

    private void follow(Follow followEntity, User followUser, User followingUser) {
        followRepository.save(followEntity);
        followUser.addFollower();
        followingUser.addFollowing();
        userRepository.save(followUser);
        userRepository.save(followingUser);
    }

    private void unfollow(Follow followEntity, User unfollowUser, User unfollowingUser) {
        followRepository.delete(followEntity);
        unfollowUser.cancelFollower();
        unfollowingUser.cancelFollowing();
        userRepository.save(unfollowUser);
        userRepository.save(unfollowingUser);
    }
}
