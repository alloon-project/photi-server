package com.alloon.alloonserver.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime
import java.time.LocalTime

data class FindChallengeFeedsDto @QueryProjection constructor(
    val id: Long,
    val username: String,
    val imageUrl: String,
    val createdDateTime: LocalDateTime,
    val proveTime: LocalTime,
)