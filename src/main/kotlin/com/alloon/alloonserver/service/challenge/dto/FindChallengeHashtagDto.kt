package com.alloon.alloonserver.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class FindChallengeHashtagDto @QueryProjection constructor(
    val challengeId: Long,
    val hashtag: String,
)
