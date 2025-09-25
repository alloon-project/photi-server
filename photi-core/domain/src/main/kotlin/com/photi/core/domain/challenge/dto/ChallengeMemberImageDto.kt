package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class ChallengeMemberImageDto @QueryProjection constructor(
    val imageUrl: String,
)
