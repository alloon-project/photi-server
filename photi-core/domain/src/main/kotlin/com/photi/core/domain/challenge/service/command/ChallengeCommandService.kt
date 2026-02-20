package com.photi.core.domain.challenge.service.command

import com.photi.core.domain.challenge.dto.CreateChallengeRequestDto
import com.photi.core.domain.challenge.model.repository.ChallengeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChallengeCommandService(
    private val challengeRepository: ChallengeRepository,
) {

    fun createChallenge(userId: Long, dto: CreateChallengeRequestDto) =
        challengeRepository.save(dto.toEntity())
}
