package com.photi.core.domain.user.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class OAuthInfo(

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = true, length = 15)
    val provider: OAuthProviderType?,

    @Column(nullable = true, length = 255)
    val sub: String?,
) {

    companion object {

        fun of(provider: OAuthProviderType, sub: String) = OAuthInfo(provider, sub)
    }
}
