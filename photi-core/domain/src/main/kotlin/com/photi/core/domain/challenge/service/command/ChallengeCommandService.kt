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

    fun deleteChallenge(challengeId: Long) {
        // todo 멤버, 피드, 히스토리, 좋아요 등도 함께 삭제되는지 확인
        challengeRepository.deleteById(challengeId)
    }
}
