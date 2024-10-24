package com.alloon.alloonserver.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class FindChallengeFeedCommentsDto @QueryProjection constructor(
    val id: Long,
    val username: String,
    val comment: String,
)