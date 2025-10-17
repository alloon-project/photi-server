package com.photi.core.domain.userchallengehistory.adapter

import com.photi.core.domain.challenge.port.ChallengeUserChallengeHistoryPort
import com.photi.core.domain.feed.port.FeedUserChallengeHistoryPort
import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.userchallengehistory.service.command.UserChallengeHistoryCommandService
import com.photi.core.domain.userchallengehistory.service.query.UserChallengeHistoryQueryService
import com.photi.core.domain.userchallengehistory.validator.UserChallengeHistoryValidator
import org.springframework.stereotype.Component

@Component
class UserChallengeHistoryAdapter(
    private val userChallengeHistoryCommandService: UserChallengeHistoryCommandService,
    private val userChallengeHistoryQueryService: UserChallengeHistoryQueryService,
    private val userChallengeHistoryValidator: UserChallengeHistoryValidator,
) : FeedUserChallengeHistoryPort, ChallengeUserChallengeHistoryPort {

    override fun increaseFeed(userId: Long) {
        getUserChallengeHistoryBy(userId).increaseFeed()
    }

    override fun decreaseFeed(userId: Long) {
        getUserChallengeHistoryBy(userId).decreaseFeed()
    }

    override fun increaseChallenge(userId: Long) {
        userChallengeHistoryQueryService.getUserChallengeHistoryBy(userId)
            ?.increaseChallenge(userChallengeHistoryValidator)
            ?: userChallengeHistoryCommandService.createUserChallengeHistory(userId)
    }

    override fun decreaseChallenge(userId: Long) {
        getUserChallengeHistoryBy(userId).decreaseChallenge()
    }

    private fun getUserChallengeHistoryBy(userId: Long) =
        userChallengeHistoryQueryService.getUserChallengeHistoryBy(userId)
            ?: throw UserException.NotFoundUserException()
}
