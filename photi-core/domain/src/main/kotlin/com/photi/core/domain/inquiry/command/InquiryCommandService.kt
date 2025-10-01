package com.photi.core.domain.inquiry.command

import com.photi.core.domain.inquiry.dto.CreateInquiryDto
import com.photi.core.domain.inquiry.model.repository.InquiryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InquiryCommandService(
    private val inquiryRepository: InquiryRepository,
) {

    fun createInquiry(dto: CreateInquiryDto, userId: Long) {
        inquiryRepository.save(dto.toEntity(userId))
    }
}
