package com.photi.core.domain.challenge.port

interface ChallengeUserChallengeHistoryPort {

    fun increaseChallenge(userId: Long)

    fun decreaseChallenge(userId: Long)
}
