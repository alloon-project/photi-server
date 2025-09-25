package com.photi.apis.enduser.common.exception.dto

import com.photi.core.domain.common.exception.ExceptionCode

data class ErrorResponse(
    val code: String,
    val message: Any,
) {

    companion object {

        fun of(exceptionCode: ExceptionCode) =
            ErrorResponse(exceptionCode.name, exceptionCode.message)
    }
}
