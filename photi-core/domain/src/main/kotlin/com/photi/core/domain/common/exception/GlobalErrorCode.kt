package com.photi.core.domain.common.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.BAD_REQUEST
import com.photi.core.domain.common.consts.HttpErrorStatus.FORBIDDEN
import com.photi.core.domain.common.consts.HttpErrorStatus.INTERNAL_SERVER_ERROR
import com.photi.core.domain.common.consts.HttpErrorStatus.UNAUTHORIZED

enum class GlobalErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    DATE_FORMAT_INVALID(BAD_REQUEST, "DATE_FORMAT_INVALID", "올바르지 않은 날짜 형식입니다."),
    TOKEN_UNAUTHENTICATED(UNAUTHORIZED, "TOKEN_UNAUTHENTICATED", "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
    INVALID_TOKEN(UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "EXPIRED_TOKEN", "만료된 토큰입니다."),
    TOKEN_UNAUTHORIZED(FORBIDDEN, "TOKEN_UNAUTHORIZED", "권한이 없는 요청입니다."),
    EMAIL_SEND_ERROR(INTERNAL_SERVER_ERROR, "EMAIL_SEND_ERROR", "이메일 전송 중 서버 에러가 발생했습니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "SERVER_ERROR", "서버 에러가 발생했습니다.");
}
