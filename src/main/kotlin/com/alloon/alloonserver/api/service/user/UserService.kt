package com.alloon.alloonserver.api.service.user

import com.alloon.alloonserver.api.service.user.response.UserGetInfoResponse
import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) {

    /**
     * 회원 정보 조회
     * @param userId 회원 식별자
     * @throws USER_NOT_FOUND 404
     * @return 회원 정보 조회 응답
     */
    fun getInfo(userId: Long): UserGetInfoResponse {
        val user = userRepository.findFetchContact(null, null, userId)
            ?: throw CustomException(USER_NOT_FOUND)

        return UserGetInfoResponse(user)
    }
}