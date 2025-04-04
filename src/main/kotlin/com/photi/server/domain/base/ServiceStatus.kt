package com.photi.server.domain.base

enum class ServiceStatus(
    val text: String,
) {
    ACTIVE("활성화"),
    END("종료"),
    DELETED("삭제"),
}