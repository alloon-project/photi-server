package com.photi.core.domain.common.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.BAD_REQUEST
import com.photi.core.domain.common.consts.HttpErrorStatus.FORBIDDEN
import com.photi.core.domain.common.consts.HttpErrorStatus.INTERNAL_SERVER_ERROR
import com.photi.core.domain.common.consts.HttpErrorStatus.PAYLOAD_TOO_LARGE
import com.photi.core.domain.common.consts.HttpErrorStatus.UNAUTHORIZED
import com.photi.core.domain.common.consts.HttpErrorStatus.UNSUPPORTED_MEDIA_TYPE

enum class GlobalErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    DATE_FORMAT_INVALID(BAD_REQUEST, "DATE_FORMAT_INVALID", "올바르지 않은 날짜 형식입니다."),
    EMPTY_FILE_INVALID(BAD_REQUEST, "EMPTY_FILE_INVALID", "비어있는 파일은 저장할 수 없습니다."),
    TOKEN_UNAUTHENTICATED(UNAUTHORIZED, "TOKEN_UNAUTHENTICATED", "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
    INVALID_TOKEN(UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "EXPIRED_TOKEN", "만료된 토큰입니다."),
    TOKEN_UNAUTHORIZED(FORBIDDEN, "TOKEN_UNAUTHORIZED", "권한이 없는 요청입니다."),
    FILE_SIZE_EXCEED(PAYLOAD_TOO_LARGE, "FILE_SIZE_EXCEED", "파일 사이즈는 8MB 이하만 가능합니다."),
    IMAGE_TYPE_UNSUPPORTED(
        UNSUPPORTED_MEDIA_TYPE,
        "IMAGE_TYPE_UNSUPPORTED",
        "이미지는 '.jpeg', '.jpg', '.png', '.gif' 타입만 가능합니다.",
    ),
    EMAIL_SEND_ERROR(INTERNAL_SERVER_ERROR, "EMAIL_SEND_ERROR", "이메일 전송 중 서버 에러가 발생했습니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "SERVER_ERROR", "서버 에러가 발생했습니다.");
}
