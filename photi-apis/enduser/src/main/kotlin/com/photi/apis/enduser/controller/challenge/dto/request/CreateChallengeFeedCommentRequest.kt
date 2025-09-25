package com.photi.apis.enduser.controller.challenge.dto.request

import com.photi.core.domain.challenge.dto.CreateChallengeFeedCommentDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "챌린지 피드 댓글 등록 요청 객체")
data class CreateChallengeFeedCommentRequest(

    @Schema(description = "챌린지 피드 댓글", example = "멋져요")
    @field:NotBlank(message = "댓글은 필수 입력입니다.")
    @field:Size(min = 1, max = 16, message = "댓글은 1~16자만 가능합니다.")
    val comment: String,
) {

    fun toServiceDto() = CreateChallengeFeedCommentDto(comment)
}
