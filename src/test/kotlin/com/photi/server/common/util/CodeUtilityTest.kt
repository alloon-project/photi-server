package com.photi.server.common.util

import io.mockk.every
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CodeUtilityTest {

    @DisplayName("인증코드 생성이 정상 작동한다.")
    @Test
    fun givenValid_whenGetVerificationCode_thenReturn() {
        // given
        val randomCode = "3429"
        mockkObject(CodeUtility)
        every { CodeUtility.getVerificationCode() } returns randomCode

        // when
        val result = CodeUtility.getVerificationCode()

        // then
        assertThat(result).isEqualTo(randomCode)
    }

    @DisplayName("초대코드 생성이 정상 작동한다.")
    @Test
    fun givenValid_whenGetInvitationCode_thenReturn() {
        // given
        val randomCode = "ABC12"
        mockkObject(CodeUtility)
        every { CodeUtility.getInvitationCode() } returns randomCode

        // when
        val result = CodeUtility.getInvitationCode()

        // then
        assertThat(result).isEqualTo(randomCode)
    }
}