package com.alloon.alloonserver.common.response

import com.alloon.alloonserver.common.constant.ExceptionCode
import io.swagger.v3.oas.annotations.responses.ApiResponses

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponses(value = [])
annotation class ApiErrorResponses(val exceptionCodes: Array<ExceptionCode>)
