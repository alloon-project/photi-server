package com.photi.core.domain.feed.exception

import com.photi.core.domain.common.exception.PhotiException

sealed class FeedException(errorCode: FeedErrorCode) : PhotiException(errorCode) {

    class ForbiddenCreatorException : FeedException(FeedErrorCode.FEED_CREATOR_FORBIDDEN)

    class NotFoundFeedException : FeedException(FeedErrorCode.FEED_NOT_FOUND)

    class ExistsFeedException : FeedException(FeedErrorCode.EXISTING_FEED)
}
