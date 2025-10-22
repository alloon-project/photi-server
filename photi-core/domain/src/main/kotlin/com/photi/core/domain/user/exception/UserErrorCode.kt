package com.photi.core.domain.user.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.BAD_REQUEST
import com.photi.core.domain.common.consts.HttpErrorStatus.CONFLICT
import com.photi.core.domain.common.consts.HttpErrorStatus.NOT_FOUND
import com.photi.core.domain.common.consts.HttpErrorStatus.UNAUTHORIZED
import com.photi.core.domain.common.exception.BaseErrorCode

enum class UserErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    EMAIL_VERIFICATION_CODE_INVALID(
        BAD_REQUEST,
        "EMAIL_VERIFICATION_CODE_INVALID",
        "이메일 인증코드가 틀렸습니다.",
    ),
    PASSWORD_MATCH_INVALID(BAD_REQUEST, "PASSWORD_MATCH_INVALID", "비밀번호와 비밀번호 재입력이 동일하지 않습니다."),
    LOGIN_UNAUTHENTICATED(UNAUTHORIZED, "LOGIN_UNAUTHENTICATED", "아이디 또는 비밀번호가 틀렸습니다."),
    EMAIL_NOT_FOUND(NOT_FOUND, "EMAIL_NOT_FOUND", "존재하지 않는 이메일입니다."),
    USER_NOT_FOUND(NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 회원입니다."),
    EXISTING_USER(CONFLICT, "EXISTING_USER", "해당 이메일로 이미 가입된 회원이 있습니다."),
    EXISTING_EMAIL(CONFLICT, "EXISTING_EMAIL", "이미 사용중인 이메일입니다."),
    DELETED_USER(CONFLICT, "DELETED_USER", "이미 탈퇴한 회원입니다."),
    EXISTING_USERNAME(CONFLICT, "EXISTING_USERNAME", "이미 사용중인 아이디입니다."),
    UNAVAILABLE_USERNAME(CONFLICT, "UNAVAILABLE_USERNAME", "사용 불가능한 아이디입니다."),
    USERNAME_FORMAT_INVALID(
        BAD_REQUEST,
        "USERNAME_FORMAT_INVALID",
        "아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요.",
        "아이디는 5~20자만 가능하고, 정규식은 ^[a-z0-9_]+$ 입니다.",
    );
}
