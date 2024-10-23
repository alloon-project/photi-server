package com.alloon.alloonserver.service.user.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalTime

data class FindUserFeedsByDateDto @QueryProjection constructor(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val proveTime: LocalTime,
)