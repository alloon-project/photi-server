package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.dto.FindChallengeInvitationCodeDto
import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.challenge.model.StatusType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChallengeRepository : JpaRepository<Challenge, Long>, ChallengeCustomRepository {

    fun existsByIdAndServiceStatus(id: Long, status: StatusType): Boolean

    @Query("select c.name, c.invitationCode from Challenge c where c.id = :id")
    fun findInvitationCodeById(id: Long): FindChallengeInvitationCodeDto?
}
