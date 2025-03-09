package com.photi.server.domain.challenge

import com.photi.server.domain.challenge.custom.ChallengeMemberCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeMemberRepository : JpaRepository<ChallengeMember, Long>,
    ChallengeMemberCustomRepository {
}