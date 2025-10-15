package com.photi.apis.enduser.common.exception.annotation

import com.photi.core.domain.appversion.exception.AppVersionErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiErrorResponses(errorCodeClass = AppVersionErrorCode::class)
annotation class AppVersionApiErrorResponses(val errorCodes: Array<AppVersionErrorCode>)
