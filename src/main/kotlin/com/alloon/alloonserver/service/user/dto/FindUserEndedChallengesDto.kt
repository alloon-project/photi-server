package com.alloon.alloonserver.service.user.dto

import com.alloon.alloonserver.service.challenge.dto.UserImageDto
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class FindUserEndedChallengesDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val endDate: LocalDate,
    val currentMemberCnt: Int,
    var memberImages: List<UserImageDto>,
)