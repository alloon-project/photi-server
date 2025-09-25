package com.photi.core.domain.s3.model

enum class FolderType(
    val value: String,
) {
    USERS("users/"),
    CHALLENGES("challenges/"),
    FEEDS("feeds/"),
    CHALLENGE_EXAMPLES("challenges/examples"),
}