package com.alloon.alloonserver.api.controller.mission.request

import com.alloon.alloonserver.service.mission.dto.CreateMissionDto
import com.alloon.alloonserver.service.mission.dto.CreateMissionHashtagDto
import com.alloon.alloonserver.service.mission.dto.CreateMissionRuleDto
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalTime

@Schema(description = "챌린지 생성 요청 객체")
data class CreateMissionRequest(

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    @field:NotBlank(message = "이름은 필수 입력입니다.")
    @field:Size(min = 2, max = 16, message = "이름은 2~16자만 가능합니다.")
    val name: String,

    @Schema(description = "챌린지 공개 여부", defaultValue = "true")
    @field:NotNull(message = "공개 여부는 필수 입력입니다.")
    val isPublic: Boolean,

    @Schema(description = "챌린지 목표", example = "하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.")
    @field:NotBlank(message = "목표는 필수 입력입니다.")
    @field:Size(min = 10, max = 120, message = "목표는 10~120자만 가능합니다.")
    val goal: String,

    @Schema(description = "챌린지 인증 시간", example = "13:00")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "kk:mm")
    val proveTime: LocalTime,

    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    @field:Future(message = "종료 날짜는 시작 날짜보다 앞설 수 없습니다.")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val endDate: LocalDate,

    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    @field:NotBlank(message = "대표 이미지는 필수 입력입니다.")
    @field:Size(min = 1, max = 500, message = "대표 이미지는 1~500자만 가능합니다.")
    val imageUrl: String,

    @Schema(
        description = "챌린지 인증 룰 리스트", example = """
        [
            {"rule": "장소 나오게 찍기"},
            {"rule": "일주일에 3회 이상 인증하기"},
            {"rule": "얼굴 안 나오게 찍기"}
        ]
    """
    )
    @field:Size(min = 1, max = 5, message = "인증 룰은 1~5개만 가능합니다.")
    @field:Valid
    val rules: List<CreateMissionRuleRequest>,

    @Schema(
        description = "챌린지 해시태그 리스트", example = """
        [
            {"hashtag": "러닝"},
            {"hashtag": "건강"}
        ]
    """
    )
    @field:Size(min = 1, max = 3, message = "해시태그는 1~3개만 가능합니다.")
    @field:Valid
    val hashtags: List<CreateMissionHashtagRequest>,
) {

    fun toServiceDto(): CreateMissionDto {
        return CreateMissionDto(
            name,
            isPublic,
            goal,
            proveTime,
            endDate,
            imageUrl,
            rules.map {
                CreateMissionRuleDto(it.rule)
            },
            hashtags.map {
                CreateMissionHashtagDto(it.hashtag)
            },
        )
    }
}
