package com.alloon.alloonserver.service.challenge.dto

import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.challenge.ChallengeHashtag
import com.alloon.alloonserver.domain.challenge.ChallengeRule
import java.time.LocalDate
import java.time.LocalTime

data class UpdateChallengeDto(
    val name: String,
    val goal: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val rules: List<ChallengeRuleDto>,
    val hashtags: List<ChallengeHashtagDto>,
) {

    fun updateChallenge(challenge: Challenge, imageUrl: String) {
        challenge.name = name
        challenge.goal = goal
        challenge.proveTime = proveTime
        challenge.endDate = endDate
        challenge.imageUrl = imageUrl

        challenge.rules.clear()
        challenge.hashtags.clear()
        rules.forEach {
            challenge.addChallengeRule(ChallengeRule(rule = it.rule))
        }
        hashtags.forEach {
            challenge.addChallengeHashtag(ChallengeHashtag(hashtag = it.hashtag))
        }
    }
}