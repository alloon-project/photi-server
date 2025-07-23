package com.photi.server.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class SearchChallengeByNameDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val currentMemberCnt: Int,
    val endDate: LocalDate,
    var memberImages: List<String>,
)
