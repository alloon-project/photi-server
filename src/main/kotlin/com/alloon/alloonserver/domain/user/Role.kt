package com.alloon.alloonserver.domain.user

enum class Role(
    val text: String
) {
    USER("회원"),
    ADMIN("관리자"),
    MASTER("마스터");
}
