package com.photi.core.domain.challengemember.adapter

import com.photi.core.domain.challenge.port.ChallengeChallengeMemberPort
import com.photi.core.domain.challengemember.dto.RegisterChallengePersonalGoalDto
import com.photi.core.domain.challengemember.exception.ChallengeMemberException
import com.photi.core.domain.challengemember.service.command.ChallengeMemberCommandService
import com.photi.core.domain.challengemember.service.query.ChallengeMemberQueryService
import com.photi.core.domain.challengemember.validator.ChallengeMemberValidator
import com.photi.core.domain.common.exception.GlobalErrorCode
import com.photi.core.domain.common.exception.PhotiException
import com.photi.core.domain.feed.port.FeedChallengeMemberPort
import com.photi.core.domain.feedcomment.port.FeedCommentChallengeMemberPort
import com.photi.core.domain.feedlike.port.FeedLikeChallengeMemberPort
import com.photi.core.domain.report.port.ReportChallengeMemberPort
import org.springframework.stereotype.Component

@Component("CHALLENGE_MEMBER")
class ChallengeMemberAdapter(
    private val challengeMemberQueryService: ChallengeMemberQueryService,
    private val challengeMemberCommandService: ChallengeMemberCommandService,
    private val challengeMemberValidator: ChallengeMemberValidator,
) : ReportChallengeMemberPort, FeedLikeChallengeMemberPort, FeedCommentChallengeMemberPort,
    FeedChallengeMemberPort, ChallengeChallengeMemberPort {

    override fun validateExistsBy(targetId: Long) {
        challengeMemberValidator.validateExistsBy(targetId)
    }

    override fun validateIsCreator(userId: Long, challengeId: Long) {
        val challengeMember = getChallengeMemberBy(userId, challengeId)
        challengeMemberValidator.validateIsCreator(challengeMember)
    }

    override fun getChallengeMemberIdBy(userId: Long, challengeId: Long) =
        challengeMemberQueryService.getChallengeMemberIdBy(userId, challengeId)
            ?: throw ChallengeMemberException.NotFoundChallengeMemberException()

    override fun createCreator(userId: Long, challengeId: Long) {
        challengeMemberCommandService.createCreator(userId, challengeId)
    }

    override fun createMember(
        userId: Long,
        challengeId: Long,
        dto: RegisterChallengePersonalGoalDto,
    ) {
        challengeMemberValidator.validateExistsMemberBy(userId, challengeId)
        challengeMemberCommandService.createMember(userId, challengeId, dto)
    }

    override fun withdrawMember(userId: Long, challengeId: Long) {
        getChallengeMemberBy(userId, challengeId).deleted()
    }

    private fun getChallengeMemberBy(userId: Long, challengeId: Long) =
        challengeMemberQueryService.getChallengeMemberBy(userId, challengeId)
            ?: throw ChallengeMemberException.NotFoundChallengeMemberException()
}
