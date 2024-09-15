package com.alloon.alloonserver.api.controller.challenge.request

import com.alloon.alloonserver.service.challenge.dto.UpdateChallengeNameDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "챌린지 이름 수정 요청 객체")
data class UpdateChallengeNameRequest(

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    @field:NotBlank(message = "이름은 필수 입력입니다.")
    @field:Size(min = 2, max = 16, message = "이름은 2~16자만 가능합니다.")
    val name: String,
) {

    fun toServiceDto(): UpdateChallengeNameDto {
        return UpdateChallengeNameDto(name)
    }
}