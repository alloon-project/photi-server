package com.photi.server.domain.challenge

import com.photi.server.domain.challenge.custom.ChallengeCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeRepository : JpaRepository<Challenge, Long>, ChallengeCustomRepository
