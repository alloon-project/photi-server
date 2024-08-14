package com.alloon.alloonserver.domain.report

enum class ReportCategoryType(
    val text: String,
) {
    CHALLENGE("챌린지"),
    CHALLENGE_MEMBER("챌린지 파티원"),
    FEED("피드"),
    ;
}