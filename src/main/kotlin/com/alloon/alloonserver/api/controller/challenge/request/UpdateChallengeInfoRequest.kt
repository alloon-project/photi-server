package com.alloon.alloonserver.api.controller.challenge.request

import com.alloon.alloonserver.service.challenge.dto.UpdateChallengeInfoDto
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalTime

data class UpdateChallengeInfoRequest(

    @Schema(description = "챌린지 목표", example = "하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.")
    @field:NotBlank(message = "목표는 필수 입력입니다.")
    @field:Size(min = 10, max = 120, message = "목표는 10~120자만 가능합니다.")
    val goal: String,

    @Schema(description = "챌린지 인증 시간", example = "13:00")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "kk:mm")
    val proveTime: LocalTime,

    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    @field:Future(message = "종료 날짜는 당일 날짜보다 앞설 수 없습니다.")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val endDate: LocalDate,
) {

    fun toServiceDto(): UpdateChallengeInfoDto {
        return UpdateChallengeInfoDto(goal, proveTime, endDate)
    }
}