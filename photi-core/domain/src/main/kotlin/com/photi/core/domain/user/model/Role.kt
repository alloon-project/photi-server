package com.photi.core.domain.user.model

enum class Role(
    val text: String
) {
    USER("회원"),
    ADMIN("관리자"),
    MASTER("마스터");
}
