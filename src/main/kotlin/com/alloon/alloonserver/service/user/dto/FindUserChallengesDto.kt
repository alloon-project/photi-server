package com.alloon.alloonserver.service.user.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalTime

data class FindUserChallengesDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val challengeImageUrl: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val hashtags: List<String>,
    var feedImageUrl: String,
)