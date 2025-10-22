package com.photi.core.domain.challenge.dto

import com.photi.core.domain.challenge.model.Challenge
import com.photi.utils.CodeUtil.getInvitationCode
import java.time.LocalDate
import java.time.LocalTime

data class CreateChallengeRequestDto(
    val name: String,
    val isPublic: Boolean,
    val goal: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val imageUrl: String,
    val rules: List<ChallengeRuleDto>,
    val hashtags: List<ChallengeHashtagDto>,
) {

    fun toEntity(): Challenge {
        val challenge = Challenge(
            name = name,
            isPublic = isPublic,
            goal = goal,
            proveTime = proveTime,
            endDate = endDate,
            imageUrl = imageUrl,
            invitationCode = getInvitationCode(isPublic),
        )
        challenge.addRules(rules)
        challenge.addHashtags(hashtags)
        return challenge
    }
}

data class CreateChallengeDto(
    val id: Long,
    val name: String,
    val goal: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val imageUrl: String,
    val rules: List<ChallengeRuleDto>,
    val hashtags: List<ChallengeHashtagDto>,
) {

    companion object {

        fun of(challenge: Challenge) = CreateChallengeDto(
            challenge.id!!,
            challenge.name,
            challenge.goal,
            challenge.proveTime,
            challenge.endDate,
            challenge.imageUrl,
            ChallengeRuleDto.of(challenge.rules),
            ChallengeHashtagDto.of(challenge.hashtags),
        )
    }
}
