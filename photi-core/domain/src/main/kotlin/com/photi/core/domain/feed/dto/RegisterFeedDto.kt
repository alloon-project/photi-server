package com.photi.core.domain.feed.dto

import com.photi.core.domain.feed.model.Feed
import java.time.LocalDateTime

data class RegisterFeedRequestDto(
    val imageUrl: String,
) {

    fun toEntity(userId: Long, challengeMemberId: Long, challengeId: Long) = Feed(
        userId = userId,
        challengeMemberId = challengeMemberId,
        challengeId = challengeId,
        imageUrl = imageUrl,
    )
}

data class RegisterFeedDto(
    val id: Long,
    val imageUrl: String,
    val createdDateTime: LocalDateTime,
    val isLike: Boolean = false,
) {

    companion object {

        fun of(feed: Feed) = RegisterFeedDto(feed.id!!, feed.imageUrl, feed.createdDateTime!!)
    }
}
