package com.photi.core.domain.userchallengehistory.service.query

import com.photi.core.domain.userchallengehistory.model.repository.UserChallengeHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserChallengeHistoryQueryService(
    private val userChallengeHistoryRepository: UserChallengeHistoryRepository,
) {

    fun getUserChallengeHistoryBy(userId: Long) =
        userChallengeHistoryRepository.findByUserId(userId)
}
