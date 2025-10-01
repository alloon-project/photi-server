package com.photi.core.domain.inquiry.dto

import com.photi.core.domain.inquiry.model.Inquiry
import com.photi.core.domain.inquiry.model.InquiryCategoryType

data class CreateInquiryDto(
    val type: String,
    val content: String,
) {

    fun toEntity(userId: Long) = Inquiry(
        userId = userId,
        type = InquiryCategoryType.valueOf(type),
        content = content,
    )
}
