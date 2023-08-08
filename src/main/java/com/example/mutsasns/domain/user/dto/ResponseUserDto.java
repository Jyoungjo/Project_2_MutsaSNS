package com.example.mutsasns.domain.user.dto;

import com.example.mutsasns.domain.user.domain.User;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResponseUserDto {
    private String username;
    private String profileImg;

    public static ResponseUserDto fromEntity(User user) {
        ResponseUserDto dto = new ResponseUserDto();
        dto.setUsername(user.getUsername());
        dto.setProfileImg(user.getProfileImg());

        return dto;
    }
}
