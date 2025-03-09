package com.photi.server.common.util

import com.photi.server.common.constant.ExceptionCode.USERNAME_FORMAT_INVALID
import com.photi.server.common.constant.RegexPatternConstants.Companion.LOWERCASE_NUMBER_UNDERSCORE
import com.photi.server.common.response.CustomException

class RegexUtility {

    companion object {

        /**
         * 아이디 형식 검증
         * @throws USERNAME_FORMAT_INVALID 400
         */
        fun validateUsernameByRegex(username: String) {
            if (!Regex(LOWERCASE_NUMBER_UNDERSCORE).matches(username))
                throw CustomException(USERNAME_FORMAT_INVALID)
        }
    }
}