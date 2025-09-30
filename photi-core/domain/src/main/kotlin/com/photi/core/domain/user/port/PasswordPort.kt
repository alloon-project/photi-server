package com.photi.core.domain.user.port

interface PasswordPort {

    fun encode(password: String): String

    fun validateMatches(password: String, encodedPassword: String)
}
