package com.photi.core.domain.feed.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FindFeedDto @QueryProjection constructor(
    val username: String,
    val userImageUrl: String,
    val feedImageUrl: String,
    val createdDateTime: LocalDateTime,
    val likeCount: Int,
    val isLike: Boolean,
)
