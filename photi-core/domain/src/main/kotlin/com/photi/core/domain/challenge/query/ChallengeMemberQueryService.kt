package com.photi.core.domain.challenge.query

import com.photi.core.domain.challenge.model.ChallengeMemberStatus
import com.photi.core.domain.challenge.model.repository.ChallengeMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChallengeMemberQueryService(
    private val challengeMemberRepository: ChallengeMemberRepository,
) {

    fun existsBy(id: Long) =
        challengeMemberRepository.existsByIdAndStatus(id, ChallengeMemberStatus.PROGRESS)
}
