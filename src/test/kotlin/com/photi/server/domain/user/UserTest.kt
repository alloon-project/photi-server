package com.photi.server.domain.user

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
            .extracting("password", "temporaryPasswordYn")
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
            .extracting("password", "temporaryPasswordYn")
            .containsExactly(password, false)
    }

    @DisplayName("회원 이미지 변경이 정상 작동한다")
    @Test
    fun givenValid_whenChangeImageUrl_thenReturn() {
        // given
        val contact = createContact()
        val user = createUser(contact)

        val imageUrl = "https://www.google.com"

        // when
        user.changeImageUrl(imageUrl)

        // then
        assertThat(user.imageUrl).isEqualTo(imageUrl)
    }

    @DisplayName("피드 인증 횟수가 0보다 크면 피드 인증 횟수가 감소한다.")
    @Test
    fun givenFeedCntGreaterThanZero_whenDecreaseFeedCnt_thenReturn() {
        // given
        val contact = createContact()
        val user = createUser(contact)

        // when
        user.decreaseFeedCnt()

        // then
        assertThat(user.feedCnt).isEqualTo(3)
    }

    @DisplayName("피드 인증 횟수가 0이면 피드 인증 횟수가 감소하지 않는다.")
    @Test
    fun givenFeedCntZero_whenDecreaseFeedCnt_thenReturn() {
        // given
        val contact = createContact()
        val user = createUser(contact).apply { feedCnt = 0 }

        // when
        user.decreaseFeedCnt()

        // then
        assertThat(user.feedCnt).isEqualTo(0)
    }

    private fun createUser(contact: Contact): User {
        return User(
            contact = contact,
            username = "tester",
            password = "password1!",
            imageUrl = "",
            feedCnt = 4
        )
    }

    private fun createContact(): Contact {
        return Contact(email = "tester@alloon.com", verificationCode = "000000")
    }
}