package com.photi.core.domain.feedlike.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.CONFLICT
import com.photi.core.domain.common.consts.HttpErrorStatus.NOT_FOUND
import com.photi.core.domain.common.exception.BaseErrorCode

enum class FeedLikeErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    EXISTING_FEED_LIKE(CONFLICT, "EXISTING_FEED_LIKE", "이미 피드 좋아요를 완료하였습니다."),
    FEED_LIKE_NOT_FOUND(NOT_FOUND, "FEED_LIKE_NOT_FOUND", "존재하지 않는 피드 좋아요입니다.");
}
