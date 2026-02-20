package com.photi.core.domain.common.exception

interface BaseErrorCode {
    val status: Int
    val code: String
    val message: String
    val description: String?
}
