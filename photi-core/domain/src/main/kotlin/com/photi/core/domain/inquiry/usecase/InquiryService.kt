package com.photi.core.domain.inquiry.usecase

import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.inquiry.dto.CreateInquiryDto
import com.photi.core.domain.inquiry.model.repository.InquiryRepository
import com.photi.core.domain.user.model.User
import com.photi.core.domain.user.model.repository.UserRepository
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
        return userRepository.find(userId) ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
    }
}
