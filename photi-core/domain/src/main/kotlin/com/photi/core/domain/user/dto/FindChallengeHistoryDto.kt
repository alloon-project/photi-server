package com.photi.core.domain.user.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FindChallengeHistoryDto @QueryProjection constructor(
    val username: String,
    val imageUrl: String,
    val feedCount: Int,
    val endedChallengeCount: Long,
    val signInDate: LocalDateTime,
)
