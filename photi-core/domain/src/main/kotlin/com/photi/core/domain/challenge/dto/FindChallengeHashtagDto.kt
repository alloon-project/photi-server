package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class FindChallengeHashtagDto @QueryProjection constructor(
    val challengeId: Long,
    val hashtag: String,
)
