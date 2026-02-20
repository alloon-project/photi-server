package com.photi.utils

import kotlin.random.Random

object PasswordUtil {

    fun getTemporaryPassword(): String {
        val chars = ('0'..'9') + ('A'..'Z') + ('a'..'z')
        val random = Random(System.currentTimeMillis())
        return (1..8)
            .map { chars.random(random) }
            .joinToString("")
    }
}
