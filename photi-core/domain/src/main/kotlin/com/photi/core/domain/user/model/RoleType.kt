package com.photi.core.domain.user.model

enum class RoleType(private val value: String) {
    UNAUTHENTICATED_USER("미인증 회원"),
    USER("일반 회원"),
    DELETED_USER("탈퇴 회원"),
    ADMIN("관리자");

    companion object {
        private val admins = listOf("photi_ios", "photi_aos")

        fun getRole(username: String) = if (username in admins) ADMIN else USER
    }
}
