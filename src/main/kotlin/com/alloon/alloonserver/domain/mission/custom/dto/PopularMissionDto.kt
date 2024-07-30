package com.alloon.alloonserver.domain.mission.custom.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class PopularMissionDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val endDate: LocalDate,
    val imageUrl: String,
    val hashtags: List<String>,
)