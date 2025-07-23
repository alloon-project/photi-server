package com.photi.server.service.s3

enum class FolderType(
    val value: String,
) {
    USERS("users/"),
    CHALLENGES("challenges/"),
    FEEDS("feeds/"),
    CHALLENGE_EXAMPLES("challenges/examples"),
}