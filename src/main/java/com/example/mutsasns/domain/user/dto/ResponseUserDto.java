package com.example.mutsasns.domain.user.dto;

import com.example.mutsasns.domain.user.domain.User;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ResponseUserDto {
    private String username;
    private String profileImg;
    private Integer followerCount;
    private Integer followingCount;

    public static ResponseUserDto fromEntity(User user) {
        return new ResponseUserDto(
                user.getUsername(),
                user.getProfileImg(),
                user.getFollowerCount(),
                user.getFollowingCount()
        );
    }
}
