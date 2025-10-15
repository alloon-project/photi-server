package com.photi.apis.enduser.common.exception.annotation

import com.photi.core.domain.challengemember.exception.ChallengeMemberErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiErrorResponses(errorCodeClass = ChallengeMemberErrorCode::class)
annotation class ChallengeMemberApiErrorResponses(val errorCodes: Array<ChallengeMemberErrorCode>)
