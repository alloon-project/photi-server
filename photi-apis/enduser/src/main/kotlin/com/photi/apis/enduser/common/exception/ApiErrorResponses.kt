package com.photi.apis.enduser.common.exception

import com.photi.core.domain.common.exception.ExceptionCode
import io.swagger.v3.oas.annotations.responses.ApiResponses

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponses(value = [])
annotation class ApiErrorResponses(val exceptionCodes: Array<ExceptionCode>)
