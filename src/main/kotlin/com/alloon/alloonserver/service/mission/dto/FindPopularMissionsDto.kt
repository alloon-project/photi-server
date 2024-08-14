package com.alloon.alloonserver.service.mission.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class FindPopularMissionsDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val endDate: LocalDate,
    val imageUrl: String,
    val hashtags: List<String>,
)