package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class FindChallengesByNameDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val challengeMemberCount: Int,
    val endDate: LocalDate,
    var memberImages: List<String>,
)
