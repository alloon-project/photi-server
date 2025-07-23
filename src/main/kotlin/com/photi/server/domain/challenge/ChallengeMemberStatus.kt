package com.photi.server.domain.challenge

enum class ChallengeMemberStatus(
    val text: String,
) {
    PROGRESS("진행중"),
    COMPLETE("완료"),
    DELETED("탈퇴");

    fun isDeleted() = this == DELETED
}
