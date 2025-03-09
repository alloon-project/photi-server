package com.photi.server.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalTime

data class FindPopularChallengesDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val goal: String,
    val currentMemberCnt: Int,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    var hashtags: List<FindChallengeHashtagDto>,
    var memberImages: List<String>,
)