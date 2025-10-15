package com.photi.apis.enduser.common.exception.annotation

import com.photi.core.domain.common.exception.BaseErrorCode
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiErrorResponses(val errorCodeClass: KClass<out BaseErrorCode>)
