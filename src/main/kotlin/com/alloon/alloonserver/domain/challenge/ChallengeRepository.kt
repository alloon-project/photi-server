package com.alloon.alloonserver.domain.challenge

import com.alloon.alloonserver.domain.challenge.custom.ChallengeCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeRepository : JpaRepository<Challenge, Long>, ChallengeCustomRepository {
}