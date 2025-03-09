package com.photi.server.service.user.dto

import com.photi.server.service.challenge.dto.FindChallengeHashtagDto
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate
import java.time.LocalTime

data class FindUserChallengesDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val challengeImageUrl: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    var hashtags: List<FindChallengeHashtagDto>,
    var feedImageUrl: String,
    var isProve: Boolean,
)