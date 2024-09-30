package com.alloon.alloonserver.domain.challenge

import com.alloon.alloonserver.domain.challenge.custom.ChallengeCustomRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ChallengeRepository : JpaRepository<Challenge, Long>, ChallengeCustomRepository {

    @Modifying(clearAutomatically = true)
    @Query("update Challenge c set c.serviceStatus = 'END' where c.endDate < current_date")
    fun bulkServiceStatusEnd(): Int
}