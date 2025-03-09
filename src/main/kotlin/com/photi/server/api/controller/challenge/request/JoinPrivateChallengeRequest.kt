package com.photi.server.api.controller.challenge.request

import com.photi.server.service.challenge.dto.JoinPrivateChallengeDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "비공개 챌린지 참여하기 요청 객체")
data class JoinPrivateChallengeRequest(

    @Schema(description = "챌린지 초대코드", example = "478DS")
    @field:NotBlank(message = "초대코드는 필수 입력입니다.")
    val invitationCode: String,
) {

    fun toServiceDto(): JoinPrivateChallengeDto {
        return JoinPrivateChallengeDto(invitationCode)
    }
}
