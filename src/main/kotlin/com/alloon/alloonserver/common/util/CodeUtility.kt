package com.alloon.alloonserver.common.util

import kotlin.random.Random

class CodeUtility {

    companion object {

        fun getVerificationCode(): String {
            return (0..9).toList()
                .shuffled(Random(System.currentTimeMillis()))
                .take(4)
                .joinToString("")
        }

        fun getInvitationCode(): String {
            val range = (0..9) + ('A'..'Z')
            return range.shuffled(Random(System.currentTimeMillis()))
                .take(5)
                .joinToString("")
        }
    }
}