package com.example.mutsasns.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestStatus {
    ACCEPT("수락"), REJECT("거절"), PENDING("보류중");
    private final String status;
}
