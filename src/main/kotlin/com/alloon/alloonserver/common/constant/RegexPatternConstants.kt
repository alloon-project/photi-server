package com.alloon.alloonserver.common.constant

class RegexPatternConstants {

    companion object {
        const val LOWERCASE_NUMBER_UNDERSCORE = "^[a-z0-9_]+$"
        const val LETTER_NUMBER_SPECIAL_CHARACTER = "^(?=.*[A-z])(?=.*\\d)(?=.*[#$@!%&*])[A-z\\d#$@!%&*]+$"
    }
}