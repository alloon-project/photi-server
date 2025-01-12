package com.alloon.alloonserver.api.controller.challenge.request

import com.alloon.alloonserver.service.challenge.dto.JoinPrivateChallengeDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "비공개 챌린지 참여하기 요청 객체")
data class JoinPrivateChallengeRequest(

    @Schema(description = "챌린지 초대코드", example = "478DS")
    @field:NotBlank(message = "초대코드는 필수 입력입니다.")
    val invitationCode: String,

    @Schema(description = "챌린지 개인목표", example = "열심히 운동하기!!")
    @field:Size(min = 1, max = 16, message = "개인목표는 1~16자만 가능합니다.")
    val goal: String?,
) {

    fun toServiceDto(): JoinPrivateChallengeDto {
        return JoinPrivateChallengeDto(invitationCode, goal)
    }
}
