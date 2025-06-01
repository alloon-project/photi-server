package com.photi.server.service.challenge.dto

import com.photi.server.domain.challenge.Challenge
import java.time.LocalDate
import java.time.LocalTime

data class FindChallengeDto(
    val name: String,
    val goal: String,
    val imageUrl: String,
    val currentMemberCnt: Int,
    val isPublic: Boolean,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val rules: List<ChallengeRuleDto>,
    val hashtags: List<ChallengeHashtagDto>,
    val memberImages: List<ChallengeMemberImageDto>,
    val creator: String,
) {

    companion object {

        fun of(
            challenge: Challenge,
            memberImages: List<ChallengeMemberImageDto>,
            creator: FindCreatorDto,
        ): FindChallengeDto {
            return FindChallengeDto(
                challenge.name,
                challenge.goal,
                challenge.imageUrl,
                challenge.currentMemberCnt,
                challenge.isPublic,
                challenge.proveTime,
                challenge.endDate,
                ChallengeRuleDto.of(challenge.rules),
                ChallengeHashtagDto.of(challenge.hashtags),
                memberImages,
                creator.username,
            )
        }
    }
}