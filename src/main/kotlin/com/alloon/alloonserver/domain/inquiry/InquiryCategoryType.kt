package com.alloon.alloonserver.domain.inquiry

enum class InquiryCategoryType(
    val text: String
) {
    SERVICE_USE("서비스 이용 문의"),
    SUGGESTION("개선/제안 요청"),
    ERROR("오류 문의"),
    ETC("기타 문의"),
}