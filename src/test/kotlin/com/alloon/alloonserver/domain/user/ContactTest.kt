package com.alloon.alloonserver.domain.user

import com.photi.server.common.constant.ExceptionCode.EMAIL_VERIFICATION_CODE_INVALID
import com.photi.server.common.response.CustomException
import com.photi.server.domain.user.Contact
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ContactTest {

    @DisplayName("연락처 인증 코드 변경이 정상 작동한다")
    @Test
    fun givenValid_whenChangeVerificationCode_thenReturn() {
        // given
        val contact = createContact()
        val verificationCode = "000001"

        // when
        contact.changeVerificationCode(verificationCode)

        // then
        assertThat(contact)
            .extracting("verificationCode", "verifyYn")
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
        assertThat(contact.verifyYn).isTrue()
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

    @DisplayName("회원 탈퇴를 하면 탈퇴 여부와 탈퇴 날짜가 변경된다.")
    @Test
    fun givenDeleteUser_whenSoftDelete_thenReturn() {
        // given
        val contact = createContact()

        // when
        contact.softDelete()

        // then
        assertThat(contact.isDeleted).isTrue()
        assertThat(contact.deletedDate).isNotNull()
    }

    private fun createContact(): Contact {
        return Contact(email = "test@photi.com", verificationCode = "000000")
    }
}