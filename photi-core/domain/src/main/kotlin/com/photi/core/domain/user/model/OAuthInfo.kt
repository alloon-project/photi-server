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

        fun ofKakao(sub: String) = OAuthInfo(OAuthProviderType.KAKAO, sub)

        fun ofGoogle(sub: String) = OAuthInfo(OAuthProviderType.GOOGLE, sub)

        fun ofApple(sub: String) = OAuthInfo(OAuthProviderType.APPLE, sub)

        fun ofNone() = OAuthInfo(null, null)
    }
}
