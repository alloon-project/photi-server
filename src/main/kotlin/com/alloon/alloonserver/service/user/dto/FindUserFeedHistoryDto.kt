package com.alloon.alloonserver.service.user.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FindUserFeedHistoryDto @QueryProjection constructor(
    val feedId: Long,
    val challengeId: Long,
    val imageUrl: String,
    val createdDate: LocalDateTime,
    val name: String,
)