package com.example.mutsasns.global.exception;

import com.example.mutsasns.global.messages.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> exceptionHandler(CustomException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ErrorResponseDto.builder()
                        .errorCode(e.getErrorCode().name())
                        .message(e.getErrorCode().getMessage())
                        .build());
    }
}
