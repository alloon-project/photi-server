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

    // 200 OK
    EMAIL_VERIFICATION_CODE_VERIFIED(OK, "이메일 인증코드가 확인 되었습니다."),

    /**
     * Develop Controller
     */
    // 200 OK
    SERVER_OK(OK, "헬스 체크를 했습니다.")
}