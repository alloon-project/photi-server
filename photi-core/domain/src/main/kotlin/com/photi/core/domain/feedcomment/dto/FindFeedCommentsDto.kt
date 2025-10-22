package com.photi.core.domain.feedcomment.dto

import com.querydsl.core.annotations.QueryProjection

data class FindFeedCommentsDto @QueryProjection constructor(
    val id: Long,
    val username: String,
    val comment: String,
)
