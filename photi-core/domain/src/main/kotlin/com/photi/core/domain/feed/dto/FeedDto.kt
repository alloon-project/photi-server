package com.photi.core.domain.feed.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime
import java.time.LocalTime

data class FeedDto @QueryProjection constructor(
    val id: Long,
    val username: String,
    val imageUrl: String,
    val createdDateTime: LocalDateTime,
    val proveTime: LocalTime,
    val isLike: Boolean,
)
