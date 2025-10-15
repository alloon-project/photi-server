package com.photi.core.domain.feed.dto

import com.photi.core.domain.feed.model.Feed

data class RegisterFeedDto(
    val imageUrl: String,
) {

    fun toEntity(userId: Long, challengeMemberId: Long, challengeId: Long) = Feed(
        userId = userId,
        challengeMemberId = challengeMemberId,
        challengeId = challengeId,
        imageUrl = imageUrl,
    )
}
