package com.alloon.alloonserver.common.constant

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ExceptionCode(
    val httpStatus: HttpStatus,
    val message: String,
) {

    /**
     * 400 Bad Request
     */
    // @NotBlank, @NotNull
    EMAIL_FIELD_REQUIRED(BAD_REQUEST, "이메일은 필수 입력입니다."),

    // @Size
    EMAIL_LENGTH_INVALID(BAD_REQUEST, "이메일은 1~100자 이하만 가능합니다."),

    // @Pattern, @Email
    EMAIL_FORMAT_INVALID(BAD_REQUEST, "올바른 이메일 형식을 입력해 주세요."),

    // @Positive, @PositiveOrZero

    // @Min, @Max

    // Custom

    /**
     * 401 Unauthorized
     */
    TOKEN_UNAUTHENTICATED(UNAUTHORIZED, "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
    LOGIN_UNAUTHENTICATED(UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다."),
    PASSWORD_UNAUTHENTICATED(UNAUTHORIZED, "비밀번호가 틀렸습니다."),

    /**
     * 403 Forbidden
     */
    TOKEN_UNAUTHORIZED(FORBIDDEN, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
    REQUEST_FORBIDDEN(FORBIDDEN, "권한이 없는 요청입니다."),

    /**
     * 404 Not Found
     */
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 회원입니다."),

    /**
     * 405 Method Not Allowed
     */
    METHOD_DISABLED(METHOD_NOT_ALLOWED, "잘못된 요청 메소드입니다."),

    /**
     * 409 Conflict
     */
    EXISTING_EMAIL(CONFLICT, "이미 사용중인 이메일입니다."),

    /**
     * 413 Payload too large
     */
    FILE_SIZE_EXCEED(PAYLOAD_TOO_LARGE, "파일 사이즈는 8MB 이하만 가능합니다."),

    /**
     * 415 Unsupported Media Type
     */
    IMAGE_TYPE_UNSUPPORTED(UNSUPPORTED_MEDIA_TYPE, "이미지는 '.jpeg', '.jpg', 또는 '.png'만 가능합니다."),

    /**
     * 500 Internal Server Error
     */
    EMAIL_SEND_ERROR(INTERNAL_SERVER_ERROR, "이메일 전송 중 서버 에러가 발생했습니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),
    ;
}