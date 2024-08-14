package com.alloon.alloonserver.api.controller.mission.response

import com.alloon.alloonserver.service.mission.dto.FindMissionMembersDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class FindMissionMembersResponse(

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

        fun of(missionMember: FindMissionMembersDto): FindMissionMembersResponse {
            return FindMissionMembersResponse(
                missionMember.id,
                missionMember.username,
                missionMember.imageUrl,
                missionMember.isCreator,
                ChronoUnit.DAYS.between(missionMember.joinedDate.toLocalDate(), LocalDate.now()) + 1,
                missionMember.goal,
            )
        }

        fun of(missionMembers: List<FindMissionMembersDto>): List<FindMissionMembersResponse> {
            return missionMembers.map { of(it) }
        }
    }
}