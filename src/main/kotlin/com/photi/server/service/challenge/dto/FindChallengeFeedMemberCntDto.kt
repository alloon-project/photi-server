package com.photi.server.service.challenge.dto

data class FindChallengeFeedMemberCntDto(
    val feedMemberCnt: Int,
) {

    companion object {

        fun of(feedMemberCnt: Long): FindChallengeFeedMemberCntDto {
            return FindChallengeFeedMemberCntDto(feedMemberCnt.toInt())
        }
    }
}
