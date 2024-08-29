package com.alloon.alloonserver.common.response

import com.alloon.alloonserver.common.constant.ExceptionCode

class CustomException(
    val exceptionCode: ExceptionCode,
    val throwable: Throwable? = null,
) : RuntimeException()