package com.photi.apis.enduser.controller.feedcomment.dto.response

import com.photi.core.domain.feedcomment.dto.FindFeedCommentsDto
import io.swagger.v3.oas.annotations.media.Schema

data class FindFeedCommentsResponse(

    @Schema(description = "댓글 id", example = "1")
    val id: Long,

    @Schema(description = "댓글 작성자 아이디", example = "photi")
    val username: String,

    @Schema(description = "댓글 내용", example = "멋져요")
    val comment: String,
) {

    companion object {

        fun of(comments: FindFeedCommentsDto) = FindFeedCommentsResponse(
            comments.id,
            comments.username,
            comments.comment,
        )
    }
}
