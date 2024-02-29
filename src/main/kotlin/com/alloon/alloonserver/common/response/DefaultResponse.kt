package com.alloon.alloonserver.common.response

import com.alloon.alloonserver.common.constant.SuccessCode
import org.springframework.http.ResponseEntity

open class DefaultResponse(
    val code: String,
    val message: String,
) {
    companion object {
        fun toResponseEntity(successCode: SuccessCode): ResponseEntity<DefaultResponse> {
            return ResponseEntity.status(successCode.httpStatus)
                .body(DefaultResponse(successCode.name, successCode.message))
        }
    }
}

class DefaultSingleResponse(
    val data: Any,

    code: String,
    message: String,
) : DefaultResponse(code, message) {

    companion object {
        fun toResponseEntity(data: Any, successCode: SuccessCode): ResponseEntity<DefaultSingleResponse> {
            return ResponseEntity.status(successCode.httpStatus)
                .body(DefaultSingleResponse(data, successCode.name, successCode.message))
        }
    }
}

class DefaultMultiResponse<T>(
    val data: PageData<T>,

    code: String,
    message: String,
) : DefaultResponse(code, message) {

    companion object {
        fun <T> toResponseEntity(data: PageData<T>, successCode: SuccessCode): ResponseEntity<DefaultMultiResponse<T>> {
            return ResponseEntity.status(successCode.httpStatus)
                .body(DefaultMultiResponse(data, successCode.name, successCode.message))
        }
    }
}

class PageData<T>(
    val data: T,
    val total: Long,
)