package com.photi.apis.enduser.common.exception.annotation

import com.photi.core.domain.user.exception.UserErrorCode
import com.photi.core.domain.userchallengehistory.exception.UserChallengeHistoryErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiErrorResponses(errorCodeClass = UserErrorCode::class)
annotation class UserApiErrorResponses(val errorCodes: Array<UserErrorCode>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiErrorResponses(errorCodeClass = UserChallengeHistoryErrorCode::class)
annotation class UserChallengeHistoryApiErrorResponses(val errorCodes: Array<UserChallengeHistoryErrorCode>)
