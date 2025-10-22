package com.photi.core.domain.challenge.port

interface ChallengeFeedPort {

    fun existsFeedInChallengeBy(challengeId: Long): Boolean
}
