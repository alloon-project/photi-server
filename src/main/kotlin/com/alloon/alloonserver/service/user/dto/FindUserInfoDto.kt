package com.alloon.alloonserver.service.user.dto

import com.querydsl.core.annotations.QueryProjection

data class FindUserInfoDto @QueryProjection constructor(
    val imageUrl: String,
    val username: String,
    val email: String,
)