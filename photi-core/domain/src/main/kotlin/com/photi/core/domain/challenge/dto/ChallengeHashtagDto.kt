package com.photi.core.domain.challenge.dto

import com.photi.core.domain.challenge.model.ChallengeHashtag

data class ChallengeHashtagDto(
    val hashtag: String,
) {

    companion object {

        fun of(challengeHashtag: ChallengeHashtag) = ChallengeHashtagDto(challengeHashtag.hashtag)

        fun of(hashtags: List<ChallengeHashtag>) = hashtags.map { of(it) }
    }
}
