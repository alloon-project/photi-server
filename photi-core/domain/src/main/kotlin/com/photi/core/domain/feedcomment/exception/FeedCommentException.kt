package com.photi.core.domain.feedcomment.exception

import com.photi.core.domain.common.exception.PhotiException

sealed class FeedCommentException(errorCode: FeedCommentErrorCode) : PhotiException(errorCode) {

    class NotFoundFeedCommentException :
        FeedCommentException(FeedCommentErrorCode.FEED_COMMENT_NOT_FOUND)
}
