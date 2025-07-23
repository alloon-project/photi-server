package com.photi.server.common.response

import com.photi.server.common.constant.ExceptionCode
import io.swagger.v3.oas.annotations.responses.ApiResponses

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponses(value = [])
annotation class ApiErrorResponses(val exceptionCodes: Array<ExceptionCode>)
