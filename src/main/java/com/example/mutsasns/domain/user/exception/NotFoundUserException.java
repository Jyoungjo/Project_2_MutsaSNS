package com.example.mutsasns.domain.user.exception;

import com.example.mutsasns.global.exception.Status404Exception;

public class NotFoundUserException extends Status404Exception {
    public NotFoundUserException() {
        super("해당 계정을 찾을 수 없습니다.");
    }
}
