package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalTime

data class FindChallengeIntroDto @QueryProjection constructor(
    val rules: List<String>,
    val proveTime: LocalTime,
    val goal: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
