package com.example.mutsasns.domain.user.exception;

import com.example.mutsasns.global.exception.Status401Exception;

public class NotMatchPasswordException extends Status401Exception {
    public NotMatchPasswordException() {
        super("입력하신 비밀번호가 일치하지 않습니다.");
    }
}
