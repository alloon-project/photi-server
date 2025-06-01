package com.photi.server.service.user.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class UserChallengeHistoryDto @QueryProjection constructor(
    val username: String,
    val imageUrl: String,
    val feedCnt: Int,
    val endedChallengeCnt: Int,
    val registerDate: LocalDateTime,
)