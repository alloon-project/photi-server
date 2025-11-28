package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.OAuthInfo
import com.photi.core.domain.user.model.RoleType
import com.photi.core.domain.user.model.User

data class OAuthSignUpDto(
    val username: String,
) {

    fun toEntity(oAuthInfo: OAuthInfo, email: String) =
        User(
            email = email,
            oAuthInfo = oAuthInfo,
            username = username,
            role = RoleType.USER,
            isAuthenticated = true,
        )
}
