package com.photi.core.domain.feedcomment.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.NOT_FOUND
import com.photi.core.domain.common.exception.BaseErrorCode

enum class FeedCommentErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    FEED_COMMENT_NOT_FOUND(NOT_FOUND, "FEED_COMMENT_NOT_FOUND", "존재하지 않는 피드 댓글입니다.");
}
