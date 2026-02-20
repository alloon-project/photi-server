package com.photi.core.domain.user.dto

import com.querydsl.core.annotations.QueryProjection

data class FeedImageDto @QueryProjection constructor(
    val challengeId: Long,
    val imageUrl: String,
    val feedId: Long,
)
