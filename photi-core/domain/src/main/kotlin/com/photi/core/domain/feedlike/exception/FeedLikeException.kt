package com.photi.core.domain.feedlike.exception

import com.photi.core.domain.common.exception.PhotiException

sealed class FeedLikeException(errorCode: FeedLikeErrorCode) : PhotiException(errorCode) {

    class ExistsFeedLikeException : FeedLikeException(FeedLikeErrorCode.EXISTING_FEED_LIKE)

    class NotFoundFeedLikeException : FeedLikeException(FeedLikeErrorCode.FEED_LIKE_NOT_FOUND)
}
