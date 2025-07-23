package com.photi.server.domain.challenge

import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeRuleRepository : JpaRepository<ChallengeRule, Long> {
}