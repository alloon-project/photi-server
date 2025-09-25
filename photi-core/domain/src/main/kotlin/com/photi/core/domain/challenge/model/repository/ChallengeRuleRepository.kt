package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.model.ChallengeRule
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeRuleRepository : JpaRepository<ChallengeRule, Long>
