package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.model.ChallengeMember
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeMemberRepository : JpaRepository<ChallengeMember, Long>,
    ChallengeMemberCustomRepository
