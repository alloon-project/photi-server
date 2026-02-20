package com.photi.core.domain.challengehistory.model.repository

import com.photi.core.domain.challengehistory.model.ChallengeHistory
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeHistoryRepository : JpaRepository<ChallengeHistory, Long> {

    fun findByChallengeId(challengeId: Long): ChallengeHistory?
}
