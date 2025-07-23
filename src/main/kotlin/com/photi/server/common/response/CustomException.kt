package com.photi.server.common.response

import com.photi.server.common.constant.ExceptionCode

class CustomException(
    val exceptionCode: ExceptionCode,
    val throwable: Throwable? = null,
) : RuntimeException()