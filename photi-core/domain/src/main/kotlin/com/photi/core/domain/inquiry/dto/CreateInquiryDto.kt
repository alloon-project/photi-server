package com.photi.core.domain.inquiry.dto

import com.photi.core.domain.inquiry.model.Inquiry
import com.photi.core.domain.inquiry.model.InquiryCategoryType
import com.photi.core.domain.user.model.User

data class CreateInquiryDto(
    val type: String,
    val content: String,
) {

    fun toEntity(user: User) =
        Inquiry(type = InquiryCategoryType.valueOf(type), content = content, user = user)
}
