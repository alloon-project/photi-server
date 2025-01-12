package com.alloon.alloonserver.api.controller.challenge.request

import com.alloon.alloonserver.service.challenge.dto.JoinPublicChallengeDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "공개 챌린지 참여하기 요청 객체")
data class JoinPublicChallengeRequest(

    @Schema(description = "챌린지 개인목표", example = "열심히 운동하기!!")
    @field:Size(min = 1, max = 16, message = "개인목표는 1~16자만 가능합니다.")
    val goal: String?,
) {

    fun toServiceDto(): JoinPublicChallengeDto {
        return JoinPublicChallengeDto(goal)
    }
}
