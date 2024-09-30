package com.alloon.alloonserver.domain.user.custom

import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.service.user.dto.UserChallengeHistoryDto
import com.alloon.alloonserver.service.user.dto.UserInfoDto

interface UserCustomRepository {

    fun findFetchContact(email: String?, username: String?, userId: Long?): User?

    fun find(userId: Long): User?

    fun findInfoById(userId: Long): UserInfoDto?

    fun findChallengeHistoryById(userId: Long): UserChallengeHistoryDto?
}