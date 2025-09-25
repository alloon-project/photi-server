package com.photi.utils

import kotlin.random.Random

object CodeUtil {

    private const val EMPTY_CHARACTER = ""

    fun getVerificationCode(): String {
        return (0..9).toList()
            .shuffled(Random(System.currentTimeMillis()))
            .take(4)
            .joinToString(EMPTY_CHARACTER)
    }

    fun getInvitationCode(isPublic: Boolean): String {
        val range = (0..9) + ('A'..'Z')
        return if (!isPublic) {
            range.shuffled(Random(System.currentTimeMillis()))
                .take(5)
                .joinToString(EMPTY_CHARACTER)
        } else EMPTY_CHARACTER
    }
}
