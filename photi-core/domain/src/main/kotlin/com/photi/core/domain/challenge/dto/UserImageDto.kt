package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class UserImageDto @QueryProjection constructor(
    val challengeId: Long,
    val imageUrl: String,
    val feedId: Long,
)
