package com.alloon.alloonserver.service.user

import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.email.EmailService
import com.alloon.alloonserver.service.user.dto.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
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
        val request = createValidContactServiceSendVerificationDto()

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
        val request = createValidContactServiceSendVerificationDto()

        // when
        authService.sendVerificationCode(request)

        // then
        val foundContact = contactRepository.findByEmail(request.email)
        assertAll(
            { assertThat(foundContact?.email).isEqualTo(request.email) },
            { assertThat(foundContact?.verificationCode).isNotEqualTo(verificationCode) }
        )
    }

    @DisplayName("가입된 이메일로 이메일 인증코드를 전송하면 예외가 발생한다")
    @Test
    fun givenRegisteredEmail_whenSendVerificationCode_thenThrow() {
        // given
        val contact = createAndSaveContact()
        createAndSaveUser(contact)
        val request = createValidContactServiceSendVerificationDto()

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
        assertThat(contact.verifyYn).isTrue()
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
        val request = createValidUserServiceValidateUsernameRequest()

        // when
        authService.validateUsername(request)

        // then
        assertThat(userRepository.existsByUsername(request.username)).isFalse()
    }

    @DisplayName("사용 불가능한 아이디로 아이디 검증을 하면 예외가 발생한다")
    @Test
    fun givenUnavailableUsername_whenValidateUsername_thenThrow() {
        // given
        val request = UserServiceValidateUsernameDto("photi")

        // when & then
        assertThatThrownBy { authService.validateUsername(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(UNAVAILABLE_USERNAME)
    }

    @DisplayName("존재하는 아이디로 아이디 검증을 하면 예외가 발생한다")
    @Test
    fun givenExistingUsername_whenValidateUsername_thenThrow() {
        // given
        val contact = createAndSaveContact()
        createAndSaveUser(contact)

        val request = createValidUserServiceValidateUsernameRequest()

        // when & then
        assertThatThrownBy { authService.validateUsername(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_USERNAME)
    }

    @DisplayName("회원가입이 정상 작동한다")
    @Test
    fun givenValid_whenRegisterUser_thenReturn() {
        // given
        val contact = createAndSaveContact()
        contact.verify(contact.verificationCode)
        contactRepository.save(contact)

        val request = createValidUserServiceRegisterRequest()

        // when
        val response = authService.registerUser(request)

        // then
        val user = userRepository.findByUsername(request.username)
            ?: throw CustomException(USER_NOT_FOUND)

        assertThat(response)
            .extracting("userId", "username")
            .containsExactly(user.id, request.username)
    }

    @DisplayName("인증을 하지 않은 이메일로 회원 가입을 하면 예외가 발생한다")
    @Test
    fun givenInvalidEmail_whenRegisterUser_thenThrow() {
        // given
        val request = createValidUserServiceRegisterRequest()

        // when & then
        assertThatThrownBy { authService.registerUser(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EMAIL_VALIDATION_INVALID)
    }

    @DisplayName("인증코드 검증을 하지 않은 이메일로 회원 가입을 하면 예외가 발생한다")
    @Test
    fun givenUnverifiedEmail_whenRegisterUser_thenThrow() {
        // given
        createAndSaveContact()

        val request = createValidUserServiceRegisterRequest()

        // when & then
        assertThatThrownBy { authService.registerUser(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EMAIL_VALIDATION_INVALID)
    }

    @DisplayName("존재하는 회원 이메일로 회원 가입을 하면 예외가 발생한다")
    @Test
    fun givenRegisteredEmail_whenRegisterUser_thenThrow() {
        // given
        val contact = createAndSaveContact()
        contact.verify(contact.verificationCode)
        createAndSaveUser(contact)

        val request = createValidUserServiceRegisterRequest()

        // when & then
        assertThatThrownBy { authService.registerUser(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_USER)
    }

    @DisplayName("아이디 찾기를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFindUsername_thenReturn() {
        // given
        val contact = createAndSaveContact()
        contact.verify(contact.verificationCode)
        val user = createAndSaveUser(contact)

        val request = createValidUserServiceFindUsernameRequest()

        // when
        authService.findUsername(request)

        // then
        val foundUser = userRepository.findFetchContact(contact.email, null, null)

        assertAll(
            {
                assertThat(foundUser)
                    .extracting(
                        "username", "password", "imageUrl", "temporaryPasswordYn", "createDateTime",
                        "updateDateTime", "contact"
                    )
                    .containsExactly(
                        user.username, user.password, user.imageUrl, user.temporaryPasswordYn,
                        user.createDateTime, user.updateDateTime, user.contact
                    )
            },
            {
                assertThat(foundUser)
                    .extracting("contact")
                    .extracting(
                        "email",
                        "verificationCode",
                        "verifyYn",
                        "createDateTime",
                        "updateDateTime"
                    )
                    .containsExactly(
                        contact.email, contact.verificationCode, contact.verifyYn,
                        contact.createDateTime, contact.updateDateTime
                    )
            }
        )
    }

    @DisplayName("가입되지 않은 이메일로 아이디 찾기를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUser_whenFindUsername_thenThrow() {
        // given
        val request = createValidUserServiceFindUsernameRequest()

        // when & then
        assertThatThrownBy { authService.findUsername(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("비밀번호 찾기가 정상 작동한다")
    @Test
    fun givenValid_whenFindPassword_thenReturn() {
        // given
        val contact = createAndSaveContact()
        contact.verify("000000")
        val user = createAndSaveUser(contact)

        val previousPassword = user.password

        val request = createValidUserServiceFindPasswordRequest()

        // when
        authService.findPassword(request)

        // then
        val foundUser = userRepository.findFetchContact(contact.email, null, null)!!

        assertAll(
            { assertThat(foundUser.password).isNotEqualTo(previousPassword) },
            { assertThat(foundUser.temporaryPasswordYn).isTrue() }
        )
    }

    @DisplayName("존재하지 않은 회원 정보로 비밀번호 찾기를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUser_whenFindPassword_thenThrow() {
        // given
        val request = createValidUserServiceFindPasswordRequest()

        // when & then
        assertThatThrownBy { authService.findPassword(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("로그인을 하면 정상 작동한다")
    @Test
    fun givenValid_whenLogin_thenReturn() {
        // given
        val contact = createAndSaveContact()
        contact.verify(contact.verificationCode)
        val user = createAndSaveUser(contact)

        val request = createValidUserServiceLoginRequest()

        // when
        val response = authService.login(request)

        // then
        assertThat(response)
            .extracting("userId", "username", "imageUrl", "temporaryPasswordYn")
            .containsExactly(user.id, user.username, user.imageUrl, user.temporaryPasswordYn)
    }

    @DisplayName("가입하지 않은 아이디로 로그인을 하면 예외가 발생한다")
    @Test
    fun givenNonRegisteredUsername_whenLogin_thenThrow() {
        // given
        val request = createValidUserServiceLoginRequest()

        // when & then
        assertThatThrownBy { authService.login(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(LOGIN_UNAUTHENTICATED)
    }

    @DisplayName("비밀번호 변경이 정상 작동한다")
    @Test
    fun givenValid_whenChangePassword_thenReturn() {
        // given
        val contact = createAndSaveContact()
        contact.verify(contact.verificationCode)
        val user = createAndSaveUser(contact)
        val encryptedPassword = user.password

        val request = createValidUserServiceChangePasswordRequest()

        // when
        authService.changePassword(user.id!!, request)

        // then
        assertThat(user.password).isNotEqualTo(encryptedPassword)
    }

    @DisplayName("존재하지 않은 회원 식별자로 비밀번호 변경을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUserId_whenChangePassword_thenThrow() {
        // given
        val userId = 1L
        val request = createValidUserServiceChangePasswordRequest()

        // when & then
        assertThatThrownBy { authService.changePassword(userId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(LOGIN_UNAUTHENTICATED)
    }

    private fun createValidUserServiceChangePasswordRequest(): UserServiceChangePasswordDto {
        return UserServiceChangePasswordDto("password1!", "password2!", "password2!")
    }

    private fun createValidUserServiceLoginRequest(): UserServiceLoginDto {
        return UserServiceLoginDto("tester", "password1!")
    }

    private fun createValidUserServiceFindPasswordRequest(): UserServiceFindPasswordDto {
        return UserServiceFindPasswordDto("tester@alloon.com", "tester")
    }

    private fun createValidUserServiceFindUsernameRequest(): UserServiceFindUsernameDto {
        return UserServiceFindUsernameDto("tester@alloon.com")
    }

    private fun createValidUserServiceRegisterRequest(): UserServiceRegisterDto {
        return UserServiceRegisterDto("tester@alloon.com", "tester", "password1!")
    }

    private fun createValidUserServiceValidateUsernameRequest(): UserServiceValidateUsernameDto {
        return UserServiceValidateUsernameDto("tester")
    }

    private fun createValidContactServiceVerifyRequest(): ContactServiceVerifyDto {
        return ContactServiceVerifyDto("tester@alloon.com", "000000")
    }

    private fun createValidContactServiceSendVerificationDto(): ContactServiceSendVerificationDto {
        return ContactServiceSendVerificationDto("tester@alloon.com")
    }

    private fun createAndSaveContact(): Contact {
        val contact =
            Contact(email = "tester@alloon.com", verificationCode = "000000", verifyYn = false)
        return contactRepository.save(contact)
    }

    private fun createAndSaveUser(contact: Contact): User {
        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        val user = User(
            contact = contact,
            username = "tester",
            password = encryptedPassword,
            imageUrl = ""
        )
        return userRepository.save(user)
    }
}