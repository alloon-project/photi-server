package com.photi.core.domain.challengemember.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.CONFLICT
import com.photi.core.domain.common.consts.HttpErrorStatus.FORBIDDEN
import com.photi.core.domain.common.consts.HttpErrorStatus.NOT_FOUND
import com.photi.core.domain.common.exception.BaseErrorCode

enum class ChallengeMemberErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    CHALLENGE_MEMBER_NOT_FOUND(NOT_FOUND, "CHALLENGE_MEMBER_NOT_FOUND", "존재하지 않는 챌린지 파티원입니다."),
    EXISTING_CHALLENGE_MEMBER(CONFLICT, "EXISTING_CHALLENGE_MEMBER", "이미 챌린지에 참여한 회원입니다."),
    CHALLENGE_CREATOR_FORBIDDEN(FORBIDDEN, "CHALLENGE_CREATOR_FORBIDDEN", "챌린지 파티장 권한이 없습니다.");
}
