package com.photi.core.domain.common.exception

open class PhotiException(
    val errorCode: BaseErrorCode,
    cause: Throwable? = null,
) : RuntimeException()
