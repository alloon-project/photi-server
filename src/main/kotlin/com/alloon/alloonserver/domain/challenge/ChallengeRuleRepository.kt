package com.alloon.alloonserver.domain.challenge

import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeRuleRepository : JpaRepository<ChallengeRule, Long> {
}