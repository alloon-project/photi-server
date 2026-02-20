package com.photi.apis.enduser.controller.challenge.dto.response

import com.photi.core.domain.challenge.dto.ChallengeHashtagDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 해시태그 응답 객체")
data class ChallengeHashtagResponse(

    @Schema(description = "챌린지 해시태그 내용")
    val hashtag: String,
) {

    companion object {

        @JvmName("ofFromDto")
        fun of(challengeHashtags: List<ChallengeHashtagDto>) = challengeHashtags.map { of(it) }

        @JvmName("ofFromString")
        fun of(hashtags: List<String>) = hashtags.map { of(it) }

        private fun of(challengeHashtag: ChallengeHashtagDto) =
            ChallengeHashtagResponse(challengeHashtag.hashtag)

        private fun of(hashtag: String) = ChallengeHashtagResponse(hashtag)
    }
}
