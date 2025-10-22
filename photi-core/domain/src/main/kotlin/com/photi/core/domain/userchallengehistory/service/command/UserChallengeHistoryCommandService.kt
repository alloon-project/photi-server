package com.photi.core.domain.userchallengehistory.service.command

import com.photi.core.domain.userchallengehistory.model.UserChallengeHistory
import com.photi.core.domain.userchallengehistory.model.repository.UserChallengeHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserChallengeHistoryCommandService(
    private val userChallengeHistoryRepository: UserChallengeHistoryRepository,
) {

    fun createUserChallengeHistory(userId: Long) {
        userChallengeHistoryRepository.save(UserChallengeHistory(userId))
    }
}
