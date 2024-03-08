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
    VERIFICATION_CODE_SENT(CREATED, "이메일 인증 코드를 보냈습니다."),

    /**
     * Develop Controller
     */
    // 200 OK
    SERVER_OK(OK, "헬스 체크를 했습니다.")
}