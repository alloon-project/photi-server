package com.photi.server.service.challenge.dto

import com.photi.server.domain.challenge.Challenge
import com.photi.server.domain.challenge.ChallengeMember
import com.photi.server.domain.user.User

data class FindChallengeInvitationCodeIsValidDto(
    val invitationCode: String,
) {

    fun toEntity(user: User, challenge: Challenge): ChallengeMember {
        return ChallengeMember(user = user, challenge = challenge, isCreator = false)
    }
}
