package com.photi.core.domain.user.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class FindEndedChallengesDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val endDate: LocalDate,
    val currentMemberCount: Int,
    var memberImages: List<String>,
)
