package com.photi.core.domain.challenge.port

interface ChallengeChallengeHistoryPort {

    fun increaseChallengeMember(challengeId: Long)

    fun increaseVisit(challengeId: Long)

    fun decreaseChallengeMember(challengeId: Long)

    fun validateLastChallengeMember(challengeId: Long): Boolean
}
