package com.alloon.alloonserver.common.constant

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK

enum class SuccessCode(
    val httpStatus: HttpStatus,
    val message: String,
) {
    /**
     * Develop Controller
     */
    // 200 OK
    SERVER_OK(OK, "헬스 체크를 했습니다.")
}