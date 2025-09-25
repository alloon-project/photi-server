package com.photi.core.domain.challenge.dto

import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.challenge.model.ChallengeHashtag
import com.photi.core.domain.challenge.model.ChallengeRule
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
