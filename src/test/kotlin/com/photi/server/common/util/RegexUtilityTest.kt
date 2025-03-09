package com.photi.server.common.util

import com.photi.server.common.constant.ExceptionCode.USERNAME_FORMAT_INVALID
import com.photi.server.common.response.CustomException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RegexUtilityTest {

    @DisplayName("올바른 아이디 형식으로 아이디 형식 검증을 하면 정상 작동한다")
    @Test
    fun givenValid_whenValidateUsernameByRegex_thenReturn() {
        // given
        val username = "tester_123"

        // when & then
        RegexUtility.validateUsernameByRegex(username)
    }

    @DisplayName("잘못된 아이디 형식으로 아이디 형식 검증을 하면 예외가 발생한다")
    @Test
    fun givenInvalid_whenValidateUsernameByRegex_thenReturn() {
        // given
        val username = "A"

        // when & then
        assertThatThrownBy { RegexUtility.validateUsernameByRegex(username) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USERNAME_FORMAT_INVALID)
    }
}