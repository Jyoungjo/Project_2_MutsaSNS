package com.example.mutsasns.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // user 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 계정을 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    USER_INCONSISTENT_PASSWORD(HttpStatus.UNAUTHORIZED, "입력하신 비밀번호가 일치하지 않습니다."),
    USER_INCONSISTENT_USERNAME_PASSWORD(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 일치하지 않습니다."),

    // article 관련 에러
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 피드를 찾을 수 없습니다."),

    // article image 관련 에러
    ARTICLE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이미지를 찾을 수 없습니다."),

    // comment 관련 에러
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),

    // token 관련 에러
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 다시 로그인 해주시기 바랍니다."),
    UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰입니다. 다시 로그인 해주시기 바랍니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다. 다시 로그인 해주시기 바랍니다."),
    ILLEGAL_ARGUMENT_JWT(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다. 다시 로그인 해주시기 바랍니다."),

    // like 관련 에러
    LIKE_NOT_PUSH_MY_ARTICLE(HttpStatus.CONFLICT, "자신이 올린 피드에는 좋아요를 누를 수 없습니다."),

    // 그 외
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
