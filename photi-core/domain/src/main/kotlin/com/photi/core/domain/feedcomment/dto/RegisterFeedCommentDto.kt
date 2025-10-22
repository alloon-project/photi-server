package com.photi.core.domain.feedcomment.dto

import com.photi.core.domain.feedcomment.model.FeedComment

data class RegisterFeedCommentRequestDto(
    val comment: String,
) {

    fun toEntity(userId: Long, challengeMemberId: Long, feedId: Long) = FeedComment(
        userId = userId,
        challengeMemberId = challengeMemberId,
        feedId = feedId,
        comment = comment,
    )
}

data class RegisterFeedCommentDto(
    val id: Long,
) {

    companion object {

        fun of(feedComment: FeedComment) = RegisterFeedCommentDto(feedComment.id!!)
    }
}
