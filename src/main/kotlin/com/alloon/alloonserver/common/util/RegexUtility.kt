package com.alloon.alloonserver.common.util

import com.alloon.alloonserver.common.constant.ExceptionCode.USERNAME_FORMAT_INVALID
import com.alloon.alloonserver.common.constant.RegexPatternConstants.LOWERCASE_NUMBER_UNDERSCORE
import com.alloon.alloonserver.common.response.CustomException

class RegexUtility {

    companion object {

        /**
         * 아이디 형식 검증
         * @throws USERNAME_FORMAT_INVALID 400
         */
        fun validateUsernameByRegex(username: String) {
            if (!Regex(LOWERCASE_NUMBER_UNDERSCORE.pattern).matches(username))
                throw CustomException(USERNAME_FORMAT_INVALID)
        }
    }
}