package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User
import com.querydsl.core.annotations.QueryProjection

data class FindChallengeCountDto @QueryProjection constructor(
    val username: String,
    val challengeCount: Int,
) {

    companion object {

        fun of(user: User) = FindChallengeCountDto(user.username ?: "", 0)
    }
}
