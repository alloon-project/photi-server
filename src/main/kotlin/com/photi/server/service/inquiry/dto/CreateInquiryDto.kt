package com.photi.server.service.inquiry.dto

import com.photi.server.domain.inquiry.Inquiry
import com.photi.server.domain.inquiry.InquiryCategoryType
import com.photi.server.domain.user.User

data class CreateInquiryDto(
    val type: String,
    val content: String,
) {

    fun toEntity(user: User): Inquiry {
        return Inquiry(type = InquiryCategoryType.valueOf(type), content = content, user = user)
    }
}