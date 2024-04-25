package com.alloon.alloonserver.domain.base

enum class ServiceStatus(
    val text: String,
) {
    ACTIVE("활성화"),
    USER_DEL("사용자 삭제"),
    ADMIN_DEL("관리자 삭제"),
}