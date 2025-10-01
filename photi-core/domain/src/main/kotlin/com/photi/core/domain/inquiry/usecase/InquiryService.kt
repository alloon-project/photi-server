package com.photi.core.domain.inquiry.usecase

import com.photi.core.domain.inquiry.command.InquiryCommandService
import com.photi.core.domain.inquiry.dto.CreateInquiryDto
import com.photi.core.domain.inquiry.port.InquiryUserPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InquiryService(
    private val inquiryUserPort: InquiryUserPort,
    private val inquiryCommandService: InquiryCommandService,
) {

    @Transactional
    fun createInquiry(userId: Long, dto: CreateInquiryDto) {
        inquiryUserPort.getUserBy(userId)
        inquiryCommandService.createInquiry(dto, userId)
    }
}
