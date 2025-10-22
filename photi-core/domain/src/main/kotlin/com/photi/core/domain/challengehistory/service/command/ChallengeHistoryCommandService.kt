package com.photi.core.domain.challengehistory.service.command

import com.photi.core.domain.challengehistory.model.ChallengeHistory
import com.photi.core.domain.challengehistory.model.repository.ChallengeHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChallengeHistoryCommandService(
    private val challengeHistoryRepository: ChallengeHistoryRepository,
) {

    fun createChallengeHistory(challengeId: Long) {
        challengeHistoryRepository.save(ChallengeHistory(challengeId))
    }
}
