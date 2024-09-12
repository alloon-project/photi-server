package com.alloon.alloonserver.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class ChallengeMemberImageDto @QueryProjection constructor(
    val imageUrl: String,
)