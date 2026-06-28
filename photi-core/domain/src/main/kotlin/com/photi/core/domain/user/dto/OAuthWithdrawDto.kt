package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.OAuthProviderType

data class OAuthWithdrawDto(
    val provider: OAuthProviderType,
    val sub: String,
)
