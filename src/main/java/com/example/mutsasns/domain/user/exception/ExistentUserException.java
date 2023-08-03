package com.example.mutsasns.domain.user.exception;

import com.example.mutsasns.global.exception.Status401Exception;

public class ExistentUserException extends Status401Exception {
    public ExistentUserException() {
        super("이미 존재하는 아이디입니다.");
    }
}
