package com.photi.core.domain.user.dto

import com.photi.core.domain.challengemember.model.StatusType
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FindFeedsByDateDto @QueryProjection constructor(
    val feedId: Long,
    val challengeId: Long,
    val imageUrl: String,
    val name: String,
    val proveTime: LocalDateTime,
    val status: StatusType,
)
