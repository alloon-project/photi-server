package com.alloon.alloonserver.api.controller.mission.response

import com.alloon.alloonserver.service.mission.dto.FindPopularMissionsDto
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "지금 인기있는 챌린지 응답 객체")
data class FindPopularMissionsResponse(

    @Schema(description = "챌린지 id", example = "1")
    val id: Long,

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    val endDate: LocalDate,

    @Schema(
        description = "챌린지 해시태그 리스트", example = """
        [
            {"hashtag": "러닝"},
            {"hashtag": "건강"}
        ]
    """
    )
    val hashtags: List<CreateMissionHashtagResponse>,
) {

    companion object {

        fun of(mission: FindPopularMissionsDto): FindPopularMissionsResponse {
            return FindPopularMissionsResponse(
                mission.id,
                mission.name,
                mission.imageUrl,
                mission.endDate,
                mission.hashtags.map {
                    CreateMissionHashtagResponse(it)
                }
            )
        }

        fun of(missions: List<FindPopularMissionsDto>): List<FindPopularMissionsResponse> = missions.map { of(it) }
    }
}