package com.photi.core.domain.inquiry.dto

import com.photi.core.domain.inquiry.model.Inquiry
import com.photi.core.domain.inquiry.model.CategoryType

data class CreateInquiryDto(
    val category: String,
    val content: String,
) {

    fun toEntity(userId: Long) = Inquiry(
        userId,
        CategoryType.valueOf(category),
        content,
    )
}
