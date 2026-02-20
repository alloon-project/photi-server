package com.photi.core.domain.userchallengehistory.model.repository

import com.photi.core.domain.userchallengehistory.model.UserChallengeHistory
import org.springframework.data.jpa.repository.JpaRepository

interface UserChallengeHistoryRepository : JpaRepository<UserChallengeHistory, Long> {

    fun findByUserId(userId: Long): UserChallengeHistory?
}
