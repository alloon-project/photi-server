package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalTime

data class FindChallengeDto @QueryProjection constructor(
    val name: String,
    val goal: String,
    val imageUrl: String,
    val currentMemberCnt: Int,
    val isPublic: Boolean,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val rules: List<String>,
    val hashtags: List<String>,
    val memberImages: List<String>,
    val creator: String,
)
