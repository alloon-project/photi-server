package com.photi.core.domain.challengehistory.service.query

import com.photi.core.domain.challengehistory.model.repository.ChallengeHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChallengeHistoryQueryService(
    private val challengeHistoryRepository: ChallengeHistoryRepository,
) {

    fun getChallengeHistoryBy(challengeId: Long) =
        challengeHistoryRepository.findByChallengeId(challengeId)
}
