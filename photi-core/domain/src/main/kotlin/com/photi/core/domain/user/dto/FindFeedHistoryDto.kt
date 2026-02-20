package com.photi.core.domain.user.dto

import com.photi.core.domain.challengemember.model.StatusType
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FindFeedHistoryDto @QueryProjection constructor(
    val feedId: Long,
    val challengeId: Long,
    val imageUrl: String,
    val createdDate: LocalDateTime,
    val name: String,
    val invitationCode: String,
    val status: StatusType,
)
