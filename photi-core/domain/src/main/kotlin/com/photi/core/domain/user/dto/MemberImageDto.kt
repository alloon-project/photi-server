package com.photi.core.domain.user.dto

import com.querydsl.core.annotations.QueryProjection

data class MemberImageDto @QueryProjection constructor(
    val challengeId: Long,
    val imageUrl: String,
)
