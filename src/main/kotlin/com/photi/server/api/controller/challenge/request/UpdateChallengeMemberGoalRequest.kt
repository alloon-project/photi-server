package com.photi.server.api.controller.challenge.request

import com.photi.server.service.challenge.dto.UpdateChallengeMemberGoalDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "챌린지 개인목표 작성 요청 객체")
data class UpdateChallengeMemberGoalRequest(

    @Schema(description = "챌린지 개인목표", example = "열심히 운동하기!!")
    @field:NotBlank(message = "개인목표는 필수 입력입니다.")
    @field:Size(min = 1, max = 16, message = "개인목표는 1~16자만 가능합니다.")
    val goal: String,
) {

    fun toServiceDto(): UpdateChallengeMemberGoalDto {
        return UpdateChallengeMemberGoalDto(goal)
    }
}