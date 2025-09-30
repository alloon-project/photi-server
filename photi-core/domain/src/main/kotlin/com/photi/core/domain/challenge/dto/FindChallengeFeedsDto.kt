package com.photi.core.domain.challenge.dto

import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.feed.model.Feed
import com.photi.core.domain.user.model.User
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime
import java.time.LocalTime

data class FindChallengeFeedsDto @QueryProjection constructor(
    val id: Long,
    val username: String,
    val imageUrl: String,
    val createdDateTime: LocalDateTime,
    val proveTime: LocalTime,
    val isLike: Boolean,
) {

    companion object {

        fun of(feed: Feed, user: User, challenge: Challenge) = FindChallengeFeedsDto(
            feed.id ?: 0L,
            user.username!!,
            feed.imageUrl,
            feed.createDateTime ?: LocalDateTime.now(),
            challenge.proveTime,
            false,
        )
    }
}
