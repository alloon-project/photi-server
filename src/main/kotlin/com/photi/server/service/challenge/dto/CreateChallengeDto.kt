package com.photi.server.service.challenge.dto

import com.photi.server.domain.challenge.Challenge
import com.photi.server.domain.challenge.ChallengeHashtag
import com.photi.server.domain.challenge.ChallengeRule
import java.time.LocalDate
import java.time.LocalTime

data class CreateChallengeDto(
    val name: String,
    val isPublic: Boolean,
    val goal: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val rules: List<ChallengeRuleDto>,
    val hashtags: List<ChallengeHashtagDto>,
    val id: Long? = null,
    val imageUrl: String? = null,
) {

    fun toEntity(imageUrl: String, invitationCode: String): Challenge {
        val challenge = Challenge(
            name = name,
            isPublic = isPublic,
            goal = goal,
            proveTime = proveTime,
            endDate = endDate,
            imageUrl = imageUrl,
            invitationCode = invitationCode,
        )
        rules.forEach {
            challenge.addChallengeRule(ChallengeRule(rule = it.rule))
        }
        hashtags.forEach {
            challenge.addChallengeHashtag(ChallengeHashtag(hashtag = it.hashtag))
        }
        return challenge
    }

    companion object {

        fun of(challenge: Challenge): CreateChallengeDto {
            return CreateChallengeDto(
                challenge.name,
                challenge.isPublic,
                challenge.goal,
                challenge.proveTime,
                challenge.endDate,
                ChallengeRuleDto.of(challenge.rules),
                ChallengeHashtagDto.of(challenge.hashtags),
                challenge.id,
                challenge.imageUrl,
            )
        }
    }
}