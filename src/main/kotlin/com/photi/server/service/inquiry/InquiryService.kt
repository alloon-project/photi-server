package com.photi.server.service.inquiry

import com.photi.server.common.constant.ExceptionCode.USER_NOT_FOUND
import com.photi.server.common.response.CustomException
import com.photi.server.domain.inquiry.InquiryRepository
import com.photi.server.domain.user.User
import com.photi.server.domain.user.UserRepository
import com.photi.server.service.inquiry.dto.CreateInquiryDto
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