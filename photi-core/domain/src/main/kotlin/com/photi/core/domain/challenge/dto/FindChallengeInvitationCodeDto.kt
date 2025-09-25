package com.photi.core.domain.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class FindChallengeInvitationCodeDto @QueryProjection constructor(
    val name: String,
    val invitationCode: String,
)
