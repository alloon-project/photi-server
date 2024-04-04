package com.alloon.alloonserver.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserTest {

    @DisplayName("회원 비밀번호를 임시 비밀번호 변경이 정상 작동한다")
    @Test
    fun givenValid_whenResetPassword_thenReturn() {
        // given
        val contact = createContact()
        val user = createUser(contact)

        val password = "password2!"

        // when
        user.resetPassword(password)

        // then
        assertThat(user)
            .extracting("password", "isTemporaryPassword")
            .containsExactly(password, true)
    }

    @DisplayName("회원 비밀번호를 새 비밀번호로 변경이 정상 작동한다")
    @Test
    fun givenValid_whenChangePassword_thenReturn() {
        // given
        val contact = createContact()
        val user = createUser(contact)

        val password = "password2!"

        // when
        user.changePassword(password)

        // then
        assertThat(user)
            .extracting("password", "isTemporaryPassword")
            .containsExactly(password, false)
    }

    private fun createUser(contact: Contact): User {
        return User(contact = contact, username = "tester", password = "password1!")
    }

    private fun createContact(): Contact {
        return Contact(email = "tester@alloon.com", verificationCode = "000000")
    }
}