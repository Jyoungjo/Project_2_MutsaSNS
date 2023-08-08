package com.example.mutsasns.domain.friend.dto;

import com.example.mutsasns.domain.friend.domain.Friend;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseFriendDto {
    private String requestUsername;
    private String status;

    public static ResponseFriendDto fromEntity(Friend friend) {
        return new ResponseFriendDto(
                friend.getRequestUser().getUsername(),
                friend.getStatus().toString()
        );
    }
}
