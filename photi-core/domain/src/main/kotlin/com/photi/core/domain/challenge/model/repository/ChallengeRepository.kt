package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.model.Challenge
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeRepository : JpaRepository<Challenge, Long>, ChallengeCustomRepository
