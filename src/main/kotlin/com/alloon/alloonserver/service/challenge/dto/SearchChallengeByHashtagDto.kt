package com.alloon.alloonserver.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class SearchChallengeByHashtagDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val currentMemberCnt: Int,
    val endDate: LocalDate,
    var hashtags: List<FindChallengeHashtagDto>,
    var memberImages: List<String>,
)
