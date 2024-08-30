package com.alloon.alloonserver.service.challenge.dto

data class CreateChallengeHashtagDto(
    val hashtag: String,
) {

    companion object {

        fun of(hashtag: String): CreateChallengeHashtagDto {
            return CreateChallengeHashtagDto(hashtag)
        }

        fun of(hashtags: List<String>): List<CreateChallengeHashtagDto> {
            return hashtags.map { of(it) }
        }
    }
}