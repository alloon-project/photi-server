package com.photi.core.domain.common.exception

class CustomException(
    val exceptionCode: ExceptionCode,
    val throwable: Throwable? = null,
) : RuntimeException()
