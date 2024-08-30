package com.alloon.alloonserver.common.util

import kotlin.random.Random

class VerificationCodeUtility {

    companion object {

        fun getVerificationCode(): String {
            return (0..9).toList()
                .shuffled(Random(System.currentTimeMillis()))
                .take(4)
                .joinToString("")
        }
    }
}