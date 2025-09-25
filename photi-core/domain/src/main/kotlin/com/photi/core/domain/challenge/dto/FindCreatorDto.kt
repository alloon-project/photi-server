package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class FindCreatorDto @QueryProjection constructor(
    val username: String,
)
