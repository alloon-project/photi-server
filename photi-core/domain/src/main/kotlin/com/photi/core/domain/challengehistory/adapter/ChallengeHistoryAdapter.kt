package com.photi.core.domain.challengehistory.adapter

import com.photi.core.domain.challenge.exception.ChallengeException
import com.photi.core.domain.challenge.port.ChallengeChallengeHistoryPort
import com.photi.core.domain.challengehistory.service.query.ChallengeHistoryQueryService
import com.photi.core.domain.challengehistory.validator.ChallengeHistoryValidator
import org.springframework.stereotype.Component

@Component
class ChallengeHistoryAdapter(
    private val challengeHistoryQueryService: ChallengeHistoryQueryService,
    private val challengeHistoryValidator: ChallengeHistoryValidator,
) : ChallengeChallengeHistoryPort {

    override fun increaseChallengeMember(challengeId: Long) {
        getChallengeHistoryBy(challengeId).increaseChallengeMember()
    }

    override fun increaseVisit(challengeId: Long) {
        getChallengeHistoryBy(challengeId).increaseVisitCount()
    }

    override fun decreaseChallengeMember(challengeId: Long) {
        getChallengeHistoryBy(challengeId).decreaseChallengeMember()
    }

    override fun validateLastChallengeMember(challengeId: Long): Boolean {
        val challengeHistory = getChallengeHistoryBy(challengeId)
        return challengeHistoryValidator.validateLastChallengeMember(challengeHistory)
    }

    private fun getChallengeHistoryBy(challengeId: Long) =
        challengeHistoryQueryService.getChallengeHistoryBy(challengeId)
            ?: throw ChallengeException.NotFoundChallengeException()
}
