package com.alloon.alloonserver.service.challenge.dto

import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.user.User

data class JoinPrivateChallengeDto(
    val invitationCode: String,
) {

    fun toEntity(user: User, challenge: Challenge): ChallengeMember {
        return ChallengeMember(user = user, challenge = challenge, isCreator = false)
    }
}
