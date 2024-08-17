package com.alloon.alloonserver.common.response

import com.alloon.alloonserver.common.constant.ExceptionCode
import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: Any,
    val path: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
) {

    companion object {

        fun of(exceptionCode: ExceptionCode, path: String): ErrorResponse {
            return ErrorResponse(exceptionCode.httpStatus.value(), exceptionCode.name, exceptionCode.message, path)
        }
    }
}