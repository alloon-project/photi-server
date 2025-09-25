package com.photi.apis.enduser.controller.challenge.dto.response

import com.photi.core.domain.challenge.dto.FindChallengeFeedCommentsDto
import io.swagger.v3.oas.annotations.media.Schema

data class FindChallengeFeedCommentsResponse(

    @Schema(description = "댓글 id", example = "1")
    val id: Long,

    @Schema(description = "댓글 작성자 아이디", example = "photi")
    val username: String,

    @Schema(description = "댓글 내용", example = "멋져요")
    val comment: String,
) {

    companion object {

        fun of(comments: FindChallengeFeedCommentsDto) = FindChallengeFeedCommentsResponse(
            comments.id,
            comments.username,
            comments.comment
        )
    }
}
