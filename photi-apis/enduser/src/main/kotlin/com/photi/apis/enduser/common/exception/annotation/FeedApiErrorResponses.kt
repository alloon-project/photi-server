package com.photi.apis.enduser.common.exception.annotation

import com.photi.core.domain.feed.exception.FeedErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiErrorResponses(errorCodeClass = FeedErrorCode::class)
annotation class FeedApiErrorResponses(val errorCodes: Array<FeedErrorCode>)
