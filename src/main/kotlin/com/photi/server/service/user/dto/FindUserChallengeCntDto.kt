package com.photi.server.service.user.dto

import com.querydsl.core.annotations.QueryProjection

data class FindUserChallengeCntDto @QueryProjection constructor(
    val username: String,
    val challengeCnt: Int,
)