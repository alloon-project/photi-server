package com.photi.core.domain.challenge.dto

data class FindChallengeFeedMemberCntDto(
    val feedMemberCnt: Int,
) {

    companion object {

        fun of(feedMemberCnt: Long) = FindChallengeFeedMemberCntDto(feedMemberCnt.toInt())
    }
}
