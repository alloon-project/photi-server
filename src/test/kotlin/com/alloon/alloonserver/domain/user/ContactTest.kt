package com.alloon.alloonserver.domain.user

import org.assertj.core.api.Assertions.assertThat
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
            .extracting("verificationCode", "isVerified")
            .containsExactly(verificationCode, false)
    }

    private fun createContact(): Contact {
        return Contact(email = "tester@alloon.com", verificationCode = "000000")
    }
}