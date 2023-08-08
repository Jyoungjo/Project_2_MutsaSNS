package com.example.mutsasns.domain.friend.dto;

import com.example.mutsasns.domain.user.domain.RequestStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RequestFriendUpdateDto {
    private RequestStatus status;
}
