package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.model.ChallengeMember
import com.photi.core.domain.challenge.model.ChallengeMemberStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeMemberRepository : JpaRepository<ChallengeMember, Long>,
    ChallengeMemberCustomRepository {

    fun existsByIdAndStatus(id: Long, status: ChallengeMemberStatus): Boolean
}
