package com.photi.apis.enduser.controller.feedcomment.dto.response

import com.photi.core.domain.feedcomment.dto.RegisterFeedCommentDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 피드 댓글 등록 응답 객체")
data class RegisterFeedCommentResponse(

    @Schema(description = "피드 댓글 id", example = "1")
    val id: Long?
) {

    companion object {

        fun of(comment: RegisterFeedCommentDto) = RegisterFeedCommentResponse(comment.id)
    }
}
