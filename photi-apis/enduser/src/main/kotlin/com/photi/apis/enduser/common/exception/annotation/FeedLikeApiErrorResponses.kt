package com.photi.apis.enduser.common.exception.annotation

import com.photi.core.domain.feedlike.exception.FeedLikeErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiErrorResponses(errorCodeClass = FeedLikeErrorCode::class)
annotation class FeedLikeApiErrorResponses(val errorCodes: Array<FeedLikeErrorCode>)
