package com.photi.core.domain.challenge.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.NOT_FOUND
import com.photi.core.domain.common.exception.BaseErrorCode

enum class ChallengeErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    CHALLENGE_NOT_FOUND(NOT_FOUND, "CHALLENGE_NOT_FOUND", "존재하지 않는 챌린지입니다.");
}
