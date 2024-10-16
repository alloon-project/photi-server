package com.alloon.alloonserver.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FindChallengeFeedDto @QueryProjection constructor(
    val username: String,
    val userImageUrl: String,
    val feedImageUrl: String,
    val createdDateTime: LocalDateTime,
    val likeCnt: Int,
)