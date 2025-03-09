package com.photi.server.common.util

import io.mockk.every
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PasswordUtilityTest {

    @DisplayName("랜덤 코드 생성이 정상 작동한다")
    @Test
    fun givenValid_whenGenerateRandomCode_thenReturn() {
        // given
        val length = 6
        val randomCode = "abc123"
        mockkObject(PasswordUtility)
        every { PasswordUtility.generateRandomCode(length) } returns randomCode

        // when
        val result = PasswordUtility.generateRandomCode(length)

        // then
        assertThat(result).isEqualTo(randomCode)
    }
}