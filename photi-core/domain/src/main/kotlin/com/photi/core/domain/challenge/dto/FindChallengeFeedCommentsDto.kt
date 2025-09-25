package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class FindChallengeFeedCommentsDto @QueryProjection constructor(
    val id: Long,
    val username: String,
    val comment: String,
)
