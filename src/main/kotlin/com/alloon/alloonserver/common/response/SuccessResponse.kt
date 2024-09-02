package com.alloon.alloonserver.common.response

data class SuccessResponse<T>(
    val code: Int,
    val message: String,
    val data: T,
)