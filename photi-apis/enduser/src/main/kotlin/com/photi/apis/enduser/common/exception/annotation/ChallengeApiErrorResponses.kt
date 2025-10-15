package com.photi.apis.enduser.common.exception.annotation

import com.photi.core.domain.challenge.exception.ChallengeErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiErrorResponses(errorCodeClass = ChallengeErrorCode::class)
annotation class ChallengeApiErrorResponses(val errorCodes: Array<ChallengeErrorCode>)
