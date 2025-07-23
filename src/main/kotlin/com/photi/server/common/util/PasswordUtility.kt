package com.photi.server.common.util

import com.photi.server.common.constant.ExceptionCode.LOGIN_UNAUTHENTICATED
import com.photi.server.common.constant.ExceptionCode.PASSWORD_MATCH_INVALID
import com.photi.server.common.response.CustomException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import kotlin.random.Random

/**
 * 패스워드 유틸리티 객체
 * @constructor passwordEncoder 구현 객체
 */
@Component
class PasswordUtility(
    private val passwordEncoder: PasswordEncoder,
) {

    companion object {
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

        /**
         * 비밀번호와 비밀번호 재입력 검증
         * @param password 비밀번호
         * @param passwordReEnter 비밀번호 재입력
         * @throws PASSWORD_MATCH_INVALID 400
         */
        fun validateMatchPassword(password: String, passwordReEnter: String) {
            if (password != passwordReEnter)
                throw CustomException(PASSWORD_MATCH_INVALID)
        }
    }

    /**
     * 비밀번호 암호화
     * @param password 비밀번호
     * @return 암호화된 비밀번호
     */
    fun encryptPassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    /**
     * 비밀번호 검증
     * @param password 비밀번호
     * @param encodedPassword 암호화된 비밀번호
     * @throws LOGIN_UNAUTHENTICATED 401
     */
    fun verifyPassword(password: String, encodedPassword: String) {
        if (!passwordEncoder.matches(password, encodedPassword))
            throw CustomException(LOGIN_UNAUTHENTICATED)
    }
}