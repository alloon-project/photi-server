package com.alloon.alloonserver.common.response

import com.alloon.alloonserver.common.constant.ExceptionCode

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