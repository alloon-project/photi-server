package com.alloon.alloonserver.service.challenge.dto

data class ChallengeHashtagDto(
    val hashtag: String,
) {

    companion object {

        fun of(hashtag: String): ChallengeHashtagDto {
            return ChallengeHashtagDto(hashtag)
        }

        fun of(hashtags: List<String>): List<ChallengeHashtagDto> {
            return hashtags.map { of(it) }
        }
    }
}