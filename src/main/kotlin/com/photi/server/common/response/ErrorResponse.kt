package com.photi.server.common.response

import com.photi.server.common.constant.ExceptionCode

data class ErrorResponse(
    val code: String,
    val message: Any,
) {

    companion object {

        fun of(exceptionCode: ExceptionCode): ErrorResponse {
            return ErrorResponse(exceptionCode.name, exceptionCode.message)
        }
    }
}