package com.alloon.alloonserver.api.controller.challenge.response

import com.alloon.alloonserver.service.challenge.dto.FindChallengeMembersDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class FindChallengeMembersResponse(

    @Schema(description = "파티원 식별자", example = "1")
    val id: Long,

    @Schema(description = "파티원 아이디", example = "photi")
    val username: String,

    @Schema(description = "파티원 프로필 이미지 url", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "파티장", example = "true")
    val isCreator: Boolean,

    @Schema(description = "파티원 활동 기간", example = "10")
    val duration: Long,

    @Schema(description = "파티원 개인목표", example = "열심히 운동하기!!")
    val goal: String?,
) {

    companion object {

        fun of(challengeMember: FindChallengeMembersDto): FindChallengeMembersResponse {
            return FindChallengeMembersResponse(
                challengeMember.id,
                challengeMember.username,
                challengeMember.imageUrl,
                challengeMember.isCreator,
                ChronoUnit.DAYS.between(challengeMember.joinedDate.toLocalDate(), LocalDate.now()) + 1,
                challengeMember.goal,
            )
        }

        fun of(challengeMembers: List<FindChallengeMembersDto>): List<FindChallengeMembersResponse> {
            return challengeMembers.map { of(it) }
        }
    }
}