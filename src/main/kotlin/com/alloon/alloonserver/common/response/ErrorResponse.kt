package com.alloon.alloonserver.common.response

import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: Any,
    val path: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)