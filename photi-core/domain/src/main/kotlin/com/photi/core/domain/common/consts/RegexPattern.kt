package com.photi.core.domain.common.consts

object RegexPattern {
    const val LOWERCASE_NUMBER_UNDERSCORE = "^[a-z0-9_]+$"
    const val LETTER_NUMBER_SPECIAL_CHARACTER =
        "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^\\sA-Za-z\\dㄱ-ㅎㅏ-ㅣ가-힣])[^\\sㄱ-ㅎㅏ-ㅣ가-힣]+$"
}
