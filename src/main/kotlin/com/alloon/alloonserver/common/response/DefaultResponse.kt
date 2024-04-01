package com.alloon.alloonserver.common.response

import com.alloon.alloonserver.common.constant.SuccessCode
import org.springframework.http.HttpHeaders
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

        fun toResponseEntity(headers: HttpHeaders, successCode: SuccessCode): ResponseEntity<DefaultResponse> {
            return ResponseEntity.status(successCode.httpStatus)
                .headers(headers)
                .body(DefaultResponse(successCode.name, successCode.message))
        }
    }
}

class DefaultSingleResponse(
    code: String,
    message: String,

    val data: Any,
) : DefaultResponse(code, message) {

    companion object {
        fun toResponseEntity(successCode: SuccessCode, data: Any): ResponseEntity<DefaultSingleResponse> {
            return ResponseEntity.status(successCode.httpStatus)
                .body(DefaultSingleResponse(successCode.name, successCode.message, data))
        }

        fun toResponseEntity(headers: HttpHeaders, successCode: SuccessCode, data: Any):
                ResponseEntity<DefaultSingleResponse> {
            return ResponseEntity.status(successCode.httpStatus)
                .headers(headers)
                .body(DefaultSingleResponse(successCode.name, successCode.message, data))
        }
    }
}

class DefaultMultiResponse<T>(
    code: String,
    message: String,

    val data: PageData<T>,
) : DefaultResponse(code, message) {

    companion object {
        fun <T> toResponseEntity(successCode: SuccessCode, data: PageData<T>): ResponseEntity<DefaultMultiResponse<T>> {
            return ResponseEntity.status(successCode.httpStatus)
                .body(DefaultMultiResponse(successCode.name, successCode.message, data))
        }
    }
}

class PageData<T>(
    val data: T,
    val total: Long,
)