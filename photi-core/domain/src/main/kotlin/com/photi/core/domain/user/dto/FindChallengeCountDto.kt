package com.photi.core.domain.user.dto

import com.querydsl.core.annotations.QueryProjection

data class FindChallengeCountDto @QueryProjection constructor(
    val username: String,
    val challengeCount: Int,
)
