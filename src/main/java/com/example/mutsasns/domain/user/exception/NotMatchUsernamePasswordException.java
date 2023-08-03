package com.example.mutsasns.domain.user.exception;

import com.example.mutsasns.global.exception.Status401Exception;

public class NotMatchUsernamePasswordException extends Status401Exception {
    public NotMatchUsernamePasswordException() {
        super("아이디 혹은 비밀번호가 일치하지 않습니다.");
    }
}
