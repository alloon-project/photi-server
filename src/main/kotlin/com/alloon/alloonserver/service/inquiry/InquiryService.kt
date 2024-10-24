package com.alloon.alloonserver.service.inquiry

import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.inquiry.InquiryRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.inquiry.dto.CreateInquiryDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InquiryService(
    private val userRepository: UserRepository,
    private val inquiryRepository: InquiryRepository,
) {

    @Transactional
    fun createInquiry(userId: Long, dto: CreateInquiryDto) {
        val user = validateUser(userId)
        val inquiry = dto.toEntity(user)

        inquiryRepository.save(inquiry)
    }

    private fun validateUser(userId: Long): User {
        return userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)
    }
}