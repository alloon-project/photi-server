package com.alloon.alloonserver.domain.feed.custom

import com.alloon.alloonserver.domain.feed.FeedComment

interface FeedCommentCustomRepository {

    fun findByCommentId(commentId: Long): FeedComment?
}