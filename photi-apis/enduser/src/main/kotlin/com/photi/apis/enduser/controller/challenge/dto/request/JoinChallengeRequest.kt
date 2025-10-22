package com.photi.apis.enduser.controller.challenge.dto.request

import com.photi.core.domain.challengemember.dto.RegisterChallengePersonalGoalDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "챌린지 참여하기 요청 객체")
data class JoinChallengeRequest(

    @Schema(description = "챌린지 개인목표", example = "열심히 운동하기!!")
    @field:Size(min = 0, max = 16, message = "개인목표는 0~16자만 가능합니다.")
    val goal: String,
) {

    fun toServiceDto() = RegisterChallengePersonalGoalDto(goal)
}
