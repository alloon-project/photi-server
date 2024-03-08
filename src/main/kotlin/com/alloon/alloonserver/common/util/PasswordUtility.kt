package com.alloon.alloonserver.common.util

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class PasswordUtility(
    private val passwordEncoder: PasswordEncoder,
) {

    /**
     * 랜덤 코드 생성
     */
    fun generateRandomCode(length: Int): String {
        val chars = ('0'..'9') + ('A'..'Z') + ('a'..'z')
        val random = Random(System.currentTimeMillis())

        return (1..length)
            .map { chars.random(random) }
            .joinToString("")
    }
}