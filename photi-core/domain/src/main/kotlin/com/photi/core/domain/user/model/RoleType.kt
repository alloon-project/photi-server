package com.photi.core.domain.user.model

enum class RoleType(
    private val value: String,
) {
    UNAUTHENTICATED_USER("미인증 회원"),
    USER("일반 회원"),
    ADMIN("관리자"),
    MASTER("마스터");
}
