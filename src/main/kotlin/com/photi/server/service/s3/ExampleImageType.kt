package com.photi.server.service.s3

enum class ExampleImageType(
    val value: String,
) {
    LUCKY("img_cover_lucky"),
    PHOTO("img_cover_photo"),
    HEALTH("img_cover_health"),
    STUDY("img_cover_study");

    companion object {

        fun getSortOrder() = entries.map { it.value }
    }
}
