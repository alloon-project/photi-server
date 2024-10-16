package com.alloon.alloonserver.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalTime

data class FindPopularChallengesDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val goal: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val hashtags: List<String>,
)