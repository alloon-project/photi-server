package com.photi.core.infra.oauth.dto

data class OidcPublicKeysResponse(
    val keys: List<Jwk>,
)

data class Jwk(
    val kid: String,
    val kty: String,
    val alg: String,
    val use: String,
    val n: String,
    val e: String,
)
