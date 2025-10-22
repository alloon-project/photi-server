package com.photi.core.domain.inquiry.service

import com.photi.core.domain.inquiry.service.command.InquiryCommandService
import com.photi.core.domain.inquiry.dto.CreateInquiryDto
import com.photi.core.domain.inquiry.port.InquiryUserPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InquiryService(
    private val inquiryCommandService: InquiryCommandService,
    private val userPort: InquiryUserPort,
) {

    @Transactional
    fun createInquiry(userId: Long, dto: CreateInquiryDto) {
        userPort.getUserBy(userId)
        inquiryCommandService.createInquiry(dto, userId)
    }
}
