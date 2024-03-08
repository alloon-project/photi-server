package com.alloon.alloonserver.api.service.user

import com.alloon.alloonserver.api.service.email.EmailService
import com.alloon.alloonserver.api.service.user.request.ContactServiceSendVerificationRequest
import com.alloon.alloonserver.api.service.user.request.ContactServiceVerifyRequest
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AuthServiceTest(
    @Autowired private val authService: AuthService,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val emailService: EmailService,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @DisplayName("이메일 인증코드 전송이 정상 작동한다")
    @Test
    fun givenValid_whenSendVerificationCode_thenReturn() {
        // given
        val request = createValidContactServiceSendVerificationRequest()

        // when
        authService.sendVerificationCode(request)

        // then
        val contact = contactRepository.findByEmail(request.email)

        assertThat(contact).extracting("email").isEqualTo(request.email)
    }

    @DisplayName("존재하는 이메일로 이메일 인증코드를 전송하면 정상 작동한다")
    @Test
    fun givenExistingEmail_whenSendVerificationCode_thenReturn() {
        // given
        val verificationCode = createAndSaveContact().verificationCode
        val request = createValidContactServiceSendVerificationRequest()

        // when
        authService.sendVerificationCode(request)

        // then
        val foundContact = contactRepository.findByEmail(request.email)
        assertAll(
            { assertThat(foundContact?.email).isEqualTo(request.email) },
            { assertThat(foundContact?.verificationCode).isNotEqualTo(verificationCode) }
        )
    }

    @DisplayName("이메일 1자 미만으로 이메일 인증코드를 전송하면 예외가 발생한다")
    @Test
    fun givenLessThan1SizeEmail__whenSendVerificationCode_thenThrow() {
        // given
        val request = createValidContactServiceSendVerificationRequest()
        request.email = ""

        // when & then
        assertThatThrownBy { authService.sendVerificationCode(request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .hasMessageContaining(EMAIL_LENGTH_INVALID.message)
    }

    @DisplayName("이메일 100자 초과인 이메일로 이메일 인증코드를 전송하면 예외가 발생한다")
    @Test
    fun givenMoreThan100SizeEmail__whenSendVerificationCode_thenThrow() {
        // given
        val request = createValidContactServiceSendVerificationRequest()
        request.email = "a".repeat(101)

        // when & then
        assertThatThrownBy { authService.sendVerificationCode(request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .hasMessageContaining(EMAIL_LENGTH_INVALID.message)
    }

    @DisplayName("잘못된 이메일 포맷으로 이메일 인증코드 전송이 예외가 발생한다")
    @Test
    fun givenFormatEmail_whenSendVerificationCode_thenThrow() {
        // given
        val request = createValidContactServiceSendVerificationRequest()
        request.email = "tester"

        // when & then
        assertThatThrownBy { authService.sendVerificationCode(request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .hasMessageContaining(EMAIL_FORMAT_INVALID.message)
    }

    @DisplayName("가입된 이메일로 이메일 인증코드를 전송하면 예외가 발생한다")
    @Test
    fun givenRegisteredEmail_whenSendVerificationCode_thenThrow() {
        // given
        val contact = createAndSaveContact()
        createAndSaveUser(contact)
        val request = createValidContactServiceSendVerificationRequest()

        // when & then
        assertThatThrownBy { authService.sendVerificationCode(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_EMAIL)
    }

    @DisplayName("올바른 인증코드로 이메일 인증코드를 검증하면 정상 작동한다")
    @Test
    fun givenValid_whenVerifyEmailVerificationCode_thenReturn() {
        // given
        val contact = createAndSaveContact()
        val request = createValidContactServiceVerifyRequest()

        // when
        authService.verifyEmailVerificationCode(request)

        // then
        assertThat(contact.isVerified).isTrue()
    }

    @DisplayName("존재하지 않는 이메일로 인증코드를 검증하면 예외가 발생한다")
    @Test
    fun givenNonExistingEmail_whenVerifyEmailVerificationCode_thenThrow() {
        // given
        val request = createValidContactServiceVerifyRequest()

        // when & then
        assertThatThrownBy { authService.verifyEmailVerificationCode(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EMAIL_NOT_FOUND)
    }

    @DisplayName("올바른 아이디로 아이디 검증을 하면 정상 작동한다")
    @Test
    fun givenValid_whenValidateUsername_thenReturn() {
        // given
        val username = "tester"

        // when
        authService.validateUsername(username)

        // then
        assertThat(userRepository.existsByUsername(username)).isFalse()
    }

    @DisplayName("사용 불가능한 아이디로 아이디 검증을 하면 예외가 발생한다")
    @Test
    fun givenUnavailableUsername_whenValidateUsername_thenThrow() {
        // given
        val username = "alloon"

        // when & then
        assertThatThrownBy{ authService.validateUsername(username) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(UNAVAILABLE_USERNAME)
    }

    @DisplayName("존재하는 아이디로 아이디 검증을 하면 예외가 발생한다")
    @Test
    fun givenExistingUsername_whenValidateUsername_thenThrow() {
        // given
        val contact = createAndSaveContact()
        val user = createAndSaveUser(contact)
        val username = user.username

        // when & then
        assertThatThrownBy { authService.validateUsername(username) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_USERNAME)
    }

    private fun createValidContactServiceVerifyRequest(): ContactServiceVerifyRequest {
        return ContactServiceVerifyRequest("tester@alloon.com", "000000")
    }

    private fun createValidContactServiceSendVerificationRequest(): ContactServiceSendVerificationRequest {
        return ContactServiceSendVerificationRequest("tester@alloon.com")
    }

    private fun createAndSaveContact(): Contact {
        val contact = Contact(email = "tester@alloon.com", verificationCode = "000000", isVerified = false)
        return contactRepository.save(contact)
    }

    private fun createAndSaveUser(contact: Contact): User {
        val user = User(contact = contact, username = "tester", password = "password")
        return userRepository.save(user)
    }
}