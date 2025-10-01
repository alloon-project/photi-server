package com.photi.core.domain.challenge.query

import com.photi.core.domain.challenge.model.repository.ChallengeRepository
import com.photi.core.domain.common.model.ServiceStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChallengeQueryService(
    private val challengeRepository: ChallengeRepository,
) {

    fun existsBy(id: Long) =
        challengeRepository.existsByIdAndServiceStatus(id, ServiceStatus.ACTIVE)
}
