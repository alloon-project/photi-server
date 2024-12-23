package com.alloon.alloonserver.service.challenge.dto

import com.alloon.alloonserver.domain.challenge.ChallengeHashtag

data class ChallengeHashtagDto(
    val hashtag: String,
) {

    companion object {

        fun of(challengeHashtag: ChallengeHashtag): ChallengeHashtagDto {
            return ChallengeHashtagDto(challengeHashtag.hashtag)
        }

        fun of(hashtags: List<ChallengeHashtag>): List<ChallengeHashtagDto> {
            return hashtags.map { of(it) }
        }
    }
}