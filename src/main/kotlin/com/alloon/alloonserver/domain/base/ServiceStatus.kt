package com.alloon.alloonserver.domain.base

enum class ServiceStatus(
    val text: String,
) {
    ACTIVE("활성화"),
    END("종료"),
    ADMIN_DEL("관리자 삭제"),
}