package com.alloon.alloonserver.api.service.user

import com.alloon.alloonserver.api.service.email.EmailService
import com.alloon.alloonserver.api.service.user.request.ContactServiceSendVerificationRequest
import com.alloon.alloonserver.common.constant.ExceptionCode.EMAIL_FORMAT_INVALID
import com.alloon.alloonserver.common.constant.ExceptionCode.EXISTING_EMAIL
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

    @DisplayName("잘못된 이메일 포맷으로 이메일 인증코드 전송이 정상 작동한다")
    @Test
    fun givenFormatEmail_whenSendVerificationCode_thenReturn() {
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