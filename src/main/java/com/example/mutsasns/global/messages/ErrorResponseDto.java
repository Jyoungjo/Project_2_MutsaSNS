package com.example.mutsasns.global.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ErrorResponseDto {
    private String errorCode;
    private String message;
}
