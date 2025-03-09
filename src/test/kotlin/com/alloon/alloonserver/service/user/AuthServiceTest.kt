package com.alloon.alloonserver.service.user

import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.constant.UnavailableConstants.UNAVAILABLE_USERNAMES
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.user.*
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.email.EmailService
import com.alloon.alloonserver.service.user.dto.*
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class AuthServiceTest {

    private val contactRepository = mockk<ContactRepository>()
    private val userRepository = mockk<UserRepository>()
    private val userRoleRepository = mockk<UserRoleRepository>()
    private val userTemplateImageRepository = mockk<UserTemplateImageRepository>()
    private val emailService = mockk<EmailService>()
    private val passwordUtility = mockk<PasswordUtility>()

    private val authService = AuthService(
        contactRepository,
        userRepository,
        userRoleRepository,
        userTemplateImageRepository,
        emailService,
        passwordUtility
    )

    @DisplayName("이메일 인증코드 전송이 정상 작동한다")
    @Test
    fun givenValid_whenSendVerificationCode_thenReturn() {
        // given
        val contact = getContact()
        val dto = ContactServiceSendVerificationDto("tester@photi.com")

        every { contactRepository.findByEmail(any()) } returns contact
        every { userRepository.existsByContact(any()) } returns false
        every { emailService.sendEmail(any(), any(), any()) } just Runs

        // when
        authService.sendVerificationCode(dto)

        // then
        assertThat(contact.email).isEqualTo(dto.email)
    }

    @DisplayName("탈퇴한 회원이 이메일 인증코드 전송을 하면 예외가 발생한다.")
    @Test
    fun givenDeletedUser_whenSendVerificationCode_thenThrow() {
        // given
        val contact = getContact().apply {
            isDeleted = true
        }
        val dto = ContactServiceSendVerificationDto("tester@photi.com")

        every { contactRepository.findByEmail(any()) } returns contact

        // when & then
        assertThatThrownBy { authService.sendVerificationCode(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(DELETED_USER)
    }

    @DisplayName("가입된 이메일로 이메일 인증코드를 전송하면 예외가 발생한다.")
    @Test
    fun givenExistingEmail_whenSendVerificationCode_thenThrow() {
        // given
        val contact = getContact()
        val dto = ContactServiceSendVerificationDto("tester@photi.com")

        every { contactRepository.findByEmail(any()) } returns contact
        every { userRepository.existsByContact(any()) } returns true

        // when & then
        assertThatThrownBy { authService.sendVerificationCode(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_EMAIL)
    }

    @DisplayName("올바른 인증코드로 이메일 인증코드를 검증하면 정상 작동한다")
    @Test
    fun givenValid_whenVerifyEmailVerificationCode_thenReturn() {
        // given
        val contact = getContact()
        val dto = ContactServiceVerifyDto("tester@photi.com", "000000")

        every { contactRepository.findByEmail(any()) } returns contact

        // when
        authService.verifyEmailVerificationCode(dto)

        // then
        assertThat(contact.verifyYn).isTrue()
    }

    @DisplayName("입력한 이메일 인증코드와 저장된 인증코드가 다를때 인증코드를 검증하면 예외가 발생한다.")
    @Test
    fun givenDifferentEmailVerificationCode_whenVerifyEmailVerificationCode_thenThrow() {
        // given
        val dto = ContactServiceVerifyDto("tester@photi.com", "000000")

        every { contactRepository.findByEmail(any())?.verify(any()) } throws CustomException(
            EMAIL_VERIFICATION_CODE_INVALID
        )

        // when & then
        assertThatThrownBy { authService.verifyEmailVerificationCode(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EMAIL_VERIFICATION_CODE_INVALID)
    }

    @DisplayName("존재하지 않는 이메일로 인증코드를 검증하면 예외가 발생한다.")
    @Test
    fun givenNotFoundEmail_whenVerifyEmailVerificationCode_thenThrow() {
        // given
        val dto = ContactServiceVerifyDto("tester@photi.com", "000000")

        every { contactRepository.findByEmail(any()) } returns null

        // when & then
        assertThatThrownBy { authService.verifyEmailVerificationCode(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EMAIL_NOT_FOUND)
    }

    @DisplayName("올바른 아이디로 아이디 검증을 하면 정상 작동한다")
    @Test
    fun givenValid_whenValidateUsername_thenReturn() {
        // given
        val dto = UserServiceValidateUsernameDto("tester")

        every { userRepository.existsByUsername(any()) } returns false

        // when
        authService.validateUsername(dto)

        // then
        assertThat(dto.username).isNotIn(UNAVAILABLE_USERNAMES)
    }

    @DisplayName("사용 불가능한 아이디로 아이디 검증을 하면 예외가 발생한다")
    @Test
    fun givenUnavailableUsername_whenValidateUsername_thenThrow() {
        // given
        val dto = UserServiceValidateUsernameDto("photi")

        // when & then
        assertThatThrownBy { authService.validateUsername(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(UNAVAILABLE_USERNAME)
    }

    @DisplayName("존재하는 아이디로 아이디 검증을 하면 예외가 발생한다")
    @Test
    fun givenExistingUsername_whenValidateUsername_thenThrow() {
        // given
        val dto = UserServiceValidateUsernameDto("tester")

        every { userRepository.existsByUsername(any()) } returns true

        // when & then
        assertThatThrownBy { authService.validateUsername(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_USERNAME)
    }

    @DisplayName("회원가입이 정상 작동한다")
    @Test
    fun givenValid_whenRegisterUser_thenReturn() {
        // given
        val contact = getContact()
        val dto = getUserServiceRegisterDto()
        val password = "encryptedPassword"
        val user = getUser()
        val userRole = UserRole(1L, user, Role.USER)

        every { contactRepository.findByEmail(any()) } returns contact
        every { userRepository.existsByContact(any()) } returns false
        every { userRepository.existsByUsername(any()) } returns false
        every { passwordUtility.encryptPassword(any()) } returns password
        every { userTemplateImageRepository.findAll() } returns listOf()
        every { userRepository.save(any()) } returns user
        every { userRoleRepository.save(any()) } returns userRole

        // when
        val response = authService.registerUser(dto)

        // then
        assertThat(response.userId).isEqualTo(user.id)
        assertThat(response.username).isEqualTo(dto.username)
    }

    @DisplayName("인증을 하지 않은 이메일로 회원 가입을 하면 예외가 발생한다")
    @Test
    fun givenInvalidEmail_whenRegisterUser_thenThrow() {
        // given
        val dto = getUserServiceRegisterDto()

        every { contactRepository.findByEmail(any()) } returns null

        // when & then
        assertThatThrownBy { authService.registerUser(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EMAIL_VALIDATION_INVALID)
    }

    @DisplayName("인증코드 검증을 하지 않은 이메일로 회원 가입을 하면 예외가 발생한다")
    @Test
    fun givenUnverifiedEmail_whenRegisterUser_thenThrow() {
        // given
        val contact = getContact().apply {
            verifyYn = false
        }
        val dto = getUserServiceRegisterDto()

        every { contactRepository.findByEmail(any()) } returns contact

        // when & then
        assertThatThrownBy { authService.registerUser(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EMAIL_VALIDATION_INVALID)
    }

    @DisplayName("존재하는 회원 이메일로 회원 가입을 하면 예외가 발생한다")
    @Test
    fun givenRegisteredEmail_whenRegisterUser_thenThrow() {
        // given
        val contact = getContact()
        val dto = getUserServiceRegisterDto()

        every { contactRepository.findByEmail(any()) } returns contact
        every { userRepository.existsByContact(any()) } returns true

        // when & then
        assertThatThrownBy { authService.registerUser(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_USER)
    }

    @DisplayName("아이디 찾기를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFindUsername_thenReturn() {
        // given
        val user = getUser()
        val dto = UserServiceFindUsernameDto("tester@photi.com")

        every { userRepository.findFetchContact(any(), any(), any()) } returns user
        every { emailService.sendEmail(any(), any(), any()) } just Runs

        // when
        authService.findUsername(dto)

        // then
        assertThat(user.contact.email).isEqualTo(dto.email)
    }

    @DisplayName("가입되지 않은 이메일로 아이디 찾기를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUser_whenFindUsername_thenThrow() {
        // given
        val dto = UserServiceFindUsernameDto("tester@photi.com")

        every { userRepository.findFetchContact(any(), any(), any()) } returns null

        // when & then
        assertThatThrownBy { authService.findUsername(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("비밀번호 찾기가 정상 작동한다")
    @Test
    fun givenValid_whenFindPassword_thenReturn() {
        // given
        val user = getUser()
        val dto = UserServiceFindPasswordDto("tester@photi.com", "tester")
        val password = "encryptPassword"

        every { userRepository.findFetchContact(any(), any(), any()) } returns user
        every { passwordUtility.encryptPassword(any()) } returns password
        every { emailService.sendEmail(any(), any(), any()) } just Runs

        // when
        authService.findPassword(dto)

        // then
        assertThat(user.password).isEqualTo(password)
    }

    @DisplayName("존재하지 않은 회원 정보로 비밀번호 찾기를 하면 예외가 발생한다")
    @Test
    fun givenNotFoundUser_whenFindPassword_thenThrow() {
        // given
        val dto = UserServiceFindPasswordDto("tester@photi.com", "tester")

        every { userRepository.findFetchContact(any(), any(), any()) } returns null

        // when & then
        assertThatThrownBy { authService.findPassword(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("로그인을 하면 정상 작동한다")
    @Test
    fun givenValid_whenLogin_thenReturn() {
        // given
        val user = getUser()
        val dto = UserServiceLoginDto("tester", "password1!")

        every { userRepository.findByUsername(any()) } returns user
        every { passwordUtility.verifyPassword(any(), any()) } just Runs

        // when
        val response = authService.login(dto)

        // then
        assertThat(response.userId).isEqualTo(user.id)
        assertThat(response.username).isEqualTo(user.username)
        assertThat(response.imageUrl).isEqualTo(user.imageUrl)
        assertThat(response.temporaryPasswordYn).isEqualTo(user.temporaryPasswordYn)
    }

    @DisplayName("가입하지 않은 아이디로 로그인을 하면 예외가 발생한다")
    @Test
    fun givenNonRegisteredUsername_whenLogin_thenThrow() {
        // given
        val dto = UserServiceLoginDto("tester", "password1!")

        every { userRepository.findByUsername(any()) } returns null

        // when & then
        assertThatThrownBy { authService.login(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(LOGIN_UNAUTHENTICATED)
    }

    @DisplayName("탈퇴한 회원이 로그인을 하면 예외가 발생한다.")
    @Test
    fun givenDeletedUser_whenLogin_thenThrow() {
        // given
        val user = getUser().apply {
            contact.isDeleted = true
        }
        val dto = UserServiceLoginDto("tester", "password1!")

        every { userRepository.findByUsername(any()) } returns user

        // when & then
        assertThatThrownBy { authService.login(dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(DELETED_USER)
    }

    @DisplayName("비밀번호 변경이 정상 작동한다")
    @Test
    fun givenValid_whenChangePassword_thenReturn() {
        // given
        val userId = 1L
        val dto = getUserServiceChangePasswordDto()
        val user = getUser()
        val password = "encryptedPassword"

        every { userRepository.find(any()) } returns user
        every { passwordUtility.verifyPassword(any(), any()) } just Runs
        every { passwordUtility.encryptPassword(any()) } returns password

        // when
        authService.changePassword(userId, dto)

        // then
        assertThat(user.password).isEqualTo(password)
    }

    @DisplayName("존재하지 않은 회원 식별자로 비밀번호 변경을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUserId_whenChangePassword_thenThrow() {
        // given
        val userId = 1L
        val dto = getUserServiceChangePasswordDto()

        every { userRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { authService.changePassword(userId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(LOGIN_UNAUTHENTICATED)
    }

    @DisplayName("회원 탈퇴를 하면 탈퇴 여부와 탈퇴 날짜가 변경된다.")
    @Test
    fun givenPassword_whenDeleteUser_thenReturn() {
        // given
        val userId = 1L
        val dto = DeleteUserDto("password1!")
        val user = getUser()

        every { userRepository.find(any()) } returns user
        every { passwordUtility.verifyPassword(any(), any()) } just Runs

        // when
        authService.deleteUser(userId, dto)

        // then
        assertThat(user.contact.isDeleted).isTrue()
        assertThat(user.contact.deletedDate).isNotNull()
    }

    @DisplayName("존재하지 않는 회원으로 회원 탈퇴를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenDeleteUser_thenReturn() {
        // given
        val userId = 1L
        val dto = DeleteUserDto("password1!")

        every { userRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { authService.deleteUser(userId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("입력한 비밀번호와 저장된 비밀번호가 다를때 회원 탈퇴를 하면 예외가 발생한다.")
    @Test
    fun givenDifferentPassword_whenDeleteUser_thenReturn() {
        // given
        val userId = 1L
        val dto = DeleteUserDto("password1!")
        val user = getUser()

        every { userRepository.find(any()) } returns user
        every { passwordUtility.verifyPassword(any(), any()) } throws CustomException(
            LOGIN_UNAUTHENTICATED
        )

        // when & then
        assertThatThrownBy { authService.deleteUser(userId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(LOGIN_UNAUTHENTICATED)
    }

    private fun getUserServiceChangePasswordDto(): UserServiceChangePasswordDto {
        return UserServiceChangePasswordDto("password1!", "password2!", "password2!")
    }

    private fun getUserServiceRegisterDto(): UserServiceRegisterDto {
        return UserServiceRegisterDto("tester@alloon.com", "tester", "password1!")
    }

    private fun getContact(): Contact {
        return Contact(1L, "tester@photi.com", "000000", true, isDeleted = false)
    }

    private fun getUser(): User {
        return User(1L, getContact(), "tester", "password1!", "")
    }
}