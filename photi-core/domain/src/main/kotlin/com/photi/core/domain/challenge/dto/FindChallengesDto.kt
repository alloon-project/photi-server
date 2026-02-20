package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class FindChallengesDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val endDate: LocalDate,
    val imageUrl: String,
    var hashtags: List<String>,
)
