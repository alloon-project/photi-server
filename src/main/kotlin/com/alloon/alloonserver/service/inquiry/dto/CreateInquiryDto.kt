package com.alloon.alloonserver.service.inquiry.dto

import com.alloon.alloonserver.domain.inquiry.Inquiry
import com.alloon.alloonserver.domain.inquiry.InquiryCategoryType
import com.alloon.alloonserver.domain.user.User

data class CreateInquiryDto(
    val type: String,
    val content: String,
) {

    fun toEntity(user: User): Inquiry {
        return Inquiry(type = InquiryCategoryType.valueOf(type), content = content, user = user)
    }
}