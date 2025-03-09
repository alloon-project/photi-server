package com.photi.server.service.challenge.dto

import com.querydsl.core.annotations.QueryProjection

data class FindChallengeInvitationCodeDto @QueryProjection constructor(
    val name: String,
    val invitationCode: String,
)