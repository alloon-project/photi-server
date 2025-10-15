package com.photi.apis.enduser.common.exception.annotation

import com.photi.core.domain.common.exception.GlobalErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiErrorResponses(errorCodeClass = GlobalErrorCode::class)
annotation class GlobalApiErrorResponses(val errorCodes: Array<GlobalErrorCode>)
