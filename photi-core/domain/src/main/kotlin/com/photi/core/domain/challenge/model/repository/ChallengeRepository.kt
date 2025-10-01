package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.common.model.ServiceStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeRepository : JpaRepository<Challenge, Long>, ChallengeCustomRepository {

    fun existsByIdAndServiceStatus(id: Long, status: ServiceStatus): Boolean
}
