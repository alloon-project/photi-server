package com.photi.core.domain.userchallengehistory.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.BAD_REQUEST
import com.photi.core.domain.common.exception.BaseErrorCode

enum class UserChallengeHistoryErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    CHALLENGE_LIMIT_EXCEED(BAD_REQUEST, "CHALLENGE_LIMIT_EXCEED", "챌린지는 최대 20개까지 참여할 수 있습니다.");
}
