package com.photi.server.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class FindCreatorDto @QueryProjection constructor(
    val username: String,
)
