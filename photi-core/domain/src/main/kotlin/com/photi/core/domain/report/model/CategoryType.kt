package com.photi.core.domain.report.model

enum class CategoryType(
    private val value: String,
) {
    CHALLENGE("챌린지"),
    CHALLENGE_MEMBER("챌린지 파티원"),
    FEED("피드"),
}
