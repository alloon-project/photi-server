package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.common.constant.ExceptionCode.EMAIL_VERIFICATION_CODE_INVALID
import com.alloon.alloonserver.common.response.CustomException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ContactTest {

    @DisplayName("연락처 인증 코드를 변경이 정상 작동한다")
    @Test
    fun givenValid_whenChangeVerificationCode_thenReturn() {
        // given
        val contact = createContact()
        val verificationCode = "000001"

        // when
        contact.changeVerificationCode(verificationCode)

        // then
        assertThat(contact)
            .extracting("verificationCode", "isVerify")
            .containsExactly(verificationCode, false)
    }

    @DisplayName("올바른 인증코드로 연락처 인증코드 검증을 하면 정상 작동한다")
    @Test
    fun givenValid_whenVerify_thenReturn() {
        // given
        val contact = createContact()
        val verificationCode = "000000"

        // when
        contact.verify(verificationCode)

        // then
        assertThat(contact.isVerify).isTrue()
    }

    @DisplayName("잘못된 인증코드로 연락처 인증코드 검증을 하면 예외가 발생한다")
    @Test
    fun givenIncorrectVerificationCode_whenVerify_thenThrow() {
        // given
        val contact = createContact()
        val verificationCode = "000001"

        // when & then
        assertThatThrownBy { contact.verify(verificationCode) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EMAIL_VERIFICATION_CODE_INVALID)
    }

    private fun createContact(): Contact {
        return Contact(email = "tester@alloon.com", verificationCode = "000000")
    }
}