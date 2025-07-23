package com.photi.server.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FindChallengeMembersDto @QueryProjection constructor(
    val id: Long,
    val username: String,
    val imageUrl: String,
    val isCreator: Boolean,
    val joinedDate: LocalDateTime,
    val goal: String?,
)