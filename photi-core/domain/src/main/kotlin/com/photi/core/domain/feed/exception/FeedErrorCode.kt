package com.photi.core.domain.feed.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.CONFLICT
import com.photi.core.domain.common.consts.HttpErrorStatus.FORBIDDEN
import com.photi.core.domain.common.consts.HttpErrorStatus.NOT_FOUND
import com.photi.core.domain.common.exception.BaseErrorCode

enum class FeedErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    FEED_CREATOR_FORBIDDEN(FORBIDDEN, "FEED_CREATOR_FORBIDDEN", "피드 삭제 권한이 없습니다."),
    FEED_NOT_FOUND(NOT_FOUND, "FEED_NOT_FOUND", "존재하지 않는 피드입니다."),
    EXISTING_FEED(CONFLICT, "EXISTING_FEED", "이미 오늘 피드 인증을 완료하였습니다.");
}
