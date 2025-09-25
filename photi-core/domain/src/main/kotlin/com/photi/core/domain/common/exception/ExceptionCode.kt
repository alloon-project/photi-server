package com.photi.core.domain.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ExceptionCode(
    val httpStatus: HttpStatus,
    val message: String,
    val description: String? = null,
) {

    // @Pattern, @Email
    EMAIL_FORMAT_INVALID(BAD_REQUEST, "올바른 이메일 형식을 입력해 주세요."),
    USERNAME_FORMAT_INVALID(
        BAD_REQUEST,
        "아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요.",
        "아이디는 5~20자만 가능하고, 정규식은 ^[a-z0-9_]+$ 입니다."
    ),
    PASSWORD_FORMAT_INVALID(BAD_REQUEST, "비밀번호는 영어, 숫자, 특수문자(#$@!%&*)의 조합으로 입력해 주세요."),
    NEW_PASSWORD_FORMAT_INVALID(BAD_REQUEST, "비밀번호는 영어, 숫자, 특수문자(#$@!%&*)의 조합으로 입력해 주세요."),
    REPORT_TYPE_INVALID(BAD_REQUEST, "신고 타입은 'CHALLENGE', 'CHALLENGE_MEMBER', 'FEED' 중 하나여야 됩니다."),
    DATE_FORMAT_INVALID(BAD_REQUEST, "올바르지 않은 날짜 형식입니다."),

    // @Positive, @PositiveOrZero

    // @Min, @Max

    // Custom
    EMAIL_VERIFICATION_CODE_INVALID(BAD_REQUEST, "이메일 인증코드가 틀렸습니다."),
    PASSWORD_MATCH_INVALID(BAD_REQUEST, "비밀번호와 비밀번호 재입력이 동일하지 않습니다."),
    EMAIL_VALIDATION_INVALID(BAD_REQUEST, "이메일 인증을 먼저 해주세요."),
    EMPTY_FILE_INVALID(BAD_REQUEST, "비어있는 파일은 저장할 수 없습니다."),
    CHALLENGE_LIMIT_EXCEED(BAD_REQUEST, "챌린지는 최대 20개까지 참여할 수 있습니다."),

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
    CHALLENGE_CREATOR_FORBIDDEN(FORBIDDEN, "챌린지 파티장 권한이 없습니다."),
    FEED_CREATOR_FORBIDDEN(FORBIDDEN, "피드 삭제 권한이 없습니다."),

    /**
     * 404 Not Found
     */
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 회원입니다."),
    EMAIL_NOT_FOUND(NOT_FOUND, "존재하지 않는 이메일입니다."),
    REPORT_CATEGORY_NOT_FOUND(NOT_FOUND, "존재하지 않는 신고 항목입니다."),
    CHALLENGE_NOT_FOUND(NOT_FOUND, "존재하지 않는 챌린지입니다."),
    CHALLENGE_MEMBER_NOT_FOUND(NOT_FOUND, "존재하지 않는 챌린지 파티원입니다."),
    FEED_NOT_FOUND(NOT_FOUND, "존재하지 않는 피드입니다."),
    FEED_COMMENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 피드 댓글입니다."),
    FEED_LIKE_NOT_FOUND(NOT_FOUND, "존재하지 않는 피드 좋아요입니다."),
    OS_POLICY_NOT_FOUND(NOT_FOUND, "존재하지 않는 OS 정책입니다."),

    /**
     * 405 Method Not Allowed
     */
    METHOD_DISABLED(METHOD_NOT_ALLOWED, "잘못된 요청 메소드입니다."),

    /**
     * 409 Conflict
     */
    EXISTING_EMAIL(CONFLICT, "이미 사용중인 이메일입니다."),
    UNAVAILABLE_USERNAME(CONFLICT, "사용 불가능한 아이디입니다."),
    EXISTING_USERNAME(CONFLICT, "이미 사용중인 아이디입니다."),
    EXISTING_USER(CONFLICT, "해당 이메일로 이미 가입된 회원이 있습니다."),
    EXISTING_FEED(CONFLICT, "이미 오늘 피드 인증을 완료하였습니다."),
    EXISTING_CHALLENGE_MEMBER(CONFLICT, "이미 챌린지에 참여한 회원입니다."),
    EXISTING_FEED_LIKE(CONFLICT, "이미 피드 좋아요를 완료하였습니다."),
    DELETED_USER(CONFLICT, "이미 탈퇴한 회원입니다."),

    /**
     * 413 Payload too large
     */
    FILE_SIZE_EXCEED(PAYLOAD_TOO_LARGE, "파일 사이즈는 8MB 이하만 가능합니다."),

    /**
     * 415 Unsupported Media Type
     */
    IMAGE_TYPE_UNSUPPORTED(
        UNSUPPORTED_MEDIA_TYPE,
        "이미지는 '.jpeg', '.jpg', '.png', '.gif' 타입만 가능합니다."
    ),

    /**
     * 500 Internal Server Error
     */
    EMAIL_SEND_ERROR(INTERNAL_SERVER_ERROR, "이메일 전송 중 서버 에러가 발생했습니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),
}
