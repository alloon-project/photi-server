package com.photi.core.domain.feed.dto

data class FindTodayFeedMemberCountDto(
    val feedMemberCount: Int,
) {

    companion object {

        fun of(count: Long) = FindTodayFeedMemberCountDto(count.toInt())
    }
}
