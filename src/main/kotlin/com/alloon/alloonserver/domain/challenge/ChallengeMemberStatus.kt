package com.alloon.alloonserver.domain.challenge

enum class ChallengeMemberStatus(
    val text: String,
) {
    PROGRESS("진행중"),
    COMPLETE("완료"),
    INCOMPLETE("미완료"),
}
