package com.alloon.alloonserver.domain.user.custom

import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.service.user.dto.FindUserChallengeCntDto
import com.alloon.alloonserver.service.user.dto.FindUserFeedsByDateDto
import com.alloon.alloonserver.service.user.dto.UserChallengeHistoryDto
import com.alloon.alloonserver.service.user.dto.UserInfoDto
import java.time.LocalDate

interface UserCustomRepository {

    fun findFetchContact(email: String?, username: String?, userId: Long?): User?

    fun find(userId: Long): User?

    fun findInfoById(userId: Long): UserInfoDto?

    fun findChallengeHistoryById(userId: Long): UserChallengeHistoryDto?

    fun findFeedsById(userId: Long): List<String>?

    fun findChallengeCntById(userId: Long): FindUserChallengeCntDto?

    fun findFeedsByDate(userId: Long, date: LocalDate): List<FindUserFeedsByDateDto>
}