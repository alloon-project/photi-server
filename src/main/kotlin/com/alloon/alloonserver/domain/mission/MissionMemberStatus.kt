package com.alloon.alloonserver.domain.mission

enum class MissionMemberStatus(
    val text: String,
) {
    PROGRESS("진행중"),
    COMPLETE("완료"),
    INCOMPLETE("미완료"),
}
