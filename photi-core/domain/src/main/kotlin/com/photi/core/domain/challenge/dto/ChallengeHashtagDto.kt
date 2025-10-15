package com.photi.core.domain.challenge.dto

import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.challenge.model.ChallengeHashtag

data class ChallengeHashtagDto(
    val hashtag: String,
) {

    fun toEntity(challenge: Challenge) = ChallengeHashtag(challenge = challenge, hashtag = hashtag)

    companion object {

        fun of(challengeHashtag: ChallengeHashtag) = ChallengeHashtagDto(challengeHashtag.hashtag)

        fun of(hashtags: List<ChallengeHashtag>) = hashtags.map { of(it) }
    }
}
