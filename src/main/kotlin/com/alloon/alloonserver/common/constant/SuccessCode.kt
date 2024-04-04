package com.alloon.alloonserver.common.constant

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK

enum class SuccessCode(
    val httpStatus: HttpStatus,
    val message: String,
) {

    /**
     * Auth Controller
     */
    // 201 Created
    EMAIL_VERIFICATION_CODE_SENT(CREATED, "이메일 인증코드를 보냈습니다."),
    USER_REGISTERED(CREATED, "회원 가입을 완료했습니다."),

    // 200 OK
    EMAIL_VERIFICATION_CODE_VERIFIED(OK, "이메일 인증코드가 확인 되었습니다."),
    USERNAME_AVAILABLE(OK, "사용 가능한 아이디입니다."),
    USERNAME_SENT(OK, "아이디를 이메일로 전송했습니다."),
    PASSWORD_SENT(OK, "임시 비밀번호를 이메일로 전송했습니다."),
    USER_LOGIN(OK, "로그인을 했습니다."),
    PASSWORD_CHANGED(OK, "비밀번호가 변경되었습니다."),
    TOKEN_REFRESHED(OK, "토큰이 재발급 됐습니다."),

    /**
     * Develop Controller
     */
    // 200 OK
    SERVER_OK(OK, "헬스 체크를 했습니다.")
}