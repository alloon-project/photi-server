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

class DefaultListResponse<T>(
    code: String,
    message: String,
    
    val data: List<T>,
) : DefaultResponse(code, message) {

    companion object {
        fun <T> toResponseEntity(successCode: SuccessCode, data: List<T>): ResponseEntity<DefaultListResponse<T>> {
            return ResponseEntity.status(successCode.httpStatus)
                .body(DefaultListResponse(successCode.name, successCode.message, data))
        }
    }
}

class DefaultPageResponse<T>(
    code: String,
    message: String,

    val total: Long,
    val data: List<T>,
) : DefaultResponse(code, message) {

    companion object {
        fun <T> toResponseEntity(successCode: SuccessCode, data: PageData<T>): ResponseEntity<DefaultPageResponse<T>> {
            return ResponseEntity.status(successCode.httpStatus)
                .body(DefaultPageResponse(successCode.name, successCode.message, data.total, data.data))
        }
    }
}

class PageData<T>(
    val data: List<T>,
    val total: Long,
)