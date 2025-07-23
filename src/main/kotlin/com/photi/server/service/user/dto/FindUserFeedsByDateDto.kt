package com.photi.server.service.user.dto

import com.photi.server.domain.challenge.ChallengeMemberStatus
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FindUserFeedsByDateDto @QueryProjection constructor(
    val feedId: Long,
    val challengeId: Long,
    val imageUrl: String,
    val name: String,
    val proveTime: LocalDateTime,
    val status: ChallengeMemberStatus,
)