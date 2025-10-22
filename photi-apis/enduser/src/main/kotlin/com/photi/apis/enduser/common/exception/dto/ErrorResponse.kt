package com.photi.apis.enduser.common.exception.dto

import com.photi.core.domain.common.exception.BaseErrorCode

data class ErrorResponse(
    val code: String,
    val message: String,
) {

    companion object {

        fun of(errorCode: BaseErrorCode) = ErrorResponse(errorCode.code, errorCode.message)
    }
}
