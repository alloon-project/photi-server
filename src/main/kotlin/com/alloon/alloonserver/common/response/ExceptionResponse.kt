package com.alloon.alloonserver.common.response

import com.alloon.alloonserver.common.constant.ExceptionCode
import org.springframework.http.ResponseEntity

class ExceptionResponse(
    val code: String,
    val message: String,
) {
    constructor(exceptionCode: ExceptionCode) : this(exceptionCode.name, exceptionCode.message)

    companion object {
        fun toResponseEntity(ex: CustomException): ResponseEntity<ExceptionResponse> {
            return ResponseEntity.status(ex.exceptionCode.httpStatus)
                .body(ExceptionResponse(ex.exceptionCode.name, ex.exceptionCode.message))
        }

        fun toResponseEntity(exceptionCode: ExceptionCode): ResponseEntity<ExceptionResponse> {
            return ResponseEntity.status(exceptionCode.httpStatus)
                .body(ExceptionResponse(exceptionCode.name, exceptionCode.message))
        }
    }
}

class CustomException(
    val exceptionCode: ExceptionCode,
    val throwable: Throwable? = null,
) : RuntimeException()