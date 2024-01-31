package com.alloon.alloonserver.api.dto.common.response

open class DefaultResponse(
    val code: String,
    val message: String,
)

class DefaultSingleResponse(
    val data: Any,

    code: String,
    message: String,
) : DefaultResponse(code, message)

class DefaultMultiResponse<T>(
    val data: PageData<T>,

    code: String,
    message: String,
) : DefaultResponse(code, message)

class PageData<T>(
    val data: T,
    val total: Long,
)