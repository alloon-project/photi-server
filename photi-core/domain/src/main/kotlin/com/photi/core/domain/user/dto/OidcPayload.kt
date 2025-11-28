package com.photi.core.domain.user.dto

data class OidcPayload(
    val iss: String,
    val aud: String,
    val sub: String,
    val email: String,
)
