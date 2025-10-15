package com.photi.apis.enduser.common.exception.annotation

import com.photi.core.domain.feedcomment.exception.FeedCommentErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiErrorResponses(errorCodeClass = FeedCommentErrorCode::class)
annotation class FeedCommentApiErrorResponses(val errorCodes: Array<FeedCommentErrorCode>)
