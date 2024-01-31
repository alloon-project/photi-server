package com.alloon.alloonserver.api.dto.common.response

import com.alloon.alloonserver.constant.ExceptionCode

class ExceptionResponse(
    val code: String,
    val message: String,
) {
    constructor(exceptionCode: ExceptionCode) : this(exceptionCode.name, exceptionCode.message)
}

class CustomException(
    val exceptionCode: ExceptionCode,
    val throwable: Throwable? = null,
) : RuntimeException()