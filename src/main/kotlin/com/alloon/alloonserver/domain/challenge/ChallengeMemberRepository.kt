package com.alloon.alloonserver.domain.challenge

import com.alloon.alloonserver.domain.challenge.custom.ChallengeMemberCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeMemberRepository : JpaRepository<ChallengeMember, Long>, ChallengeMemberCustomRepository {
}