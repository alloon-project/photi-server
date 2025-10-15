package com.photi.core.domain.challengemember.exception

import com.photi.core.domain.common.exception.PhotiException

sealed class ChallengeMemberException(errorCode: ChallengeMemberErrorCode) :
    PhotiException(errorCode) {

    class NotFoundChallengeMemberException :
        ChallengeMemberException(ChallengeMemberErrorCode.CHALLENGE_MEMBER_NOT_FOUND)

    class ExistsChallengeMemberException :
        ChallengeMemberException(ChallengeMemberErrorCode.EXISTING_CHALLENGE_MEMBER)

    class ForbiddenCreatorException :
        ChallengeMemberException(ChallengeMemberErrorCode.CHALLENGE_CREATOR_FORBIDDEN)
}
