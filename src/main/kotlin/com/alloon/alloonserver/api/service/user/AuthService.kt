package com.alloon.alloonserver.api.service.user

import com.alloon.alloonserver.api.service.email.EmailService
import com.alloon.alloonserver.api.service.user.request.*
import com.alloon.alloonserver.api.service.user.response.UserLoginResponse
import com.alloon.alloonserver.api.service.user.response.UserRegisterResponse
import com.alloon.alloonserver.common.constant.EmailConstants.*
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.constant.UnavailableConstants.UNAVAILABLE_USERNAMES
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.domain.user.UserRoleRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class AuthService(
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val emailService: EmailService,
    private val passwordUtility: PasswordUtility,
) {

    /**
     * 이메일 인증코드 전송
     * @param request 연락처 인증코드 전송 요청
     * @throws EXISTING_EMAIL 409
     * @throws EMAIL_SEND_ERROR 500
     */
    @Transactional
    fun sendVerificationCode(@Valid request: ContactServiceSendVerificationRequest) {
        val verificationCode = PasswordUtility.generateRandomCode(6)

        contactRepository.findByEmail(request.email)
            ?.let { foundContact ->
                if (userRepository.existsByContact(foundContact))
                    throw CustomException(EXISTING_EMAIL)

                foundContact.changeVerificationCode(verificationCode)
            } ?: run {
            contactRepository.save(request.toEntity(verificationCode))
        }

        emailService.sendEmail(request.email, verificationCode, REGISTER_VERIFICATION_CODE)
    }

    /**
     * 이메일 인증코드 검증
     * @param request 연락처 인증코드 검증 요청
     * @throws EMAIL_VERIFICATION_CODE_INVALID 400
     * @throws EMAIL_NOT_FOUND 404
     */
    @Transactional
    fun verifyEmailVerificationCode(@Valid request: ContactServiceVerifyRequest) {
        contactRepository.findByEmail(request.email)
            ?.verify(request.verificationCode)
            ?: throw CustomException(EMAIL_NOT_FOUND)
    }

    /**
     * 아이디 검증
     * @param request 아이디 검증 요청
     * @throws USERNAME_LENGTH_INVALID 400
     * @throws USERNAME_FORMAT_INVALID 400
     * @throws UNAVAILABLE_USERNAME 409
     * @throws EXISTING_USERNAME 409
     */
    fun validateUsername(@Valid request: UserServiceValidateUsernameRequest) {
        if (request.username in UNAVAILABLE_USERNAMES.fields)
            throw CustomException(UNAVAILABLE_USERNAME)

        if (userRepository.existsByUsername(request.username))
            throw CustomException(EXISTING_USERNAME)
    }

    /**
     * 회원 가입
     * @param request 회원 가입 요청
     * @throws EMAIL_VALIDATION_INVALID 400
     * @throws PASSWORD_MATCH_INVALID 400
     * @throws EXISTING_USER 409
     * @throws UNAVAILABLE_USERNAME 409
     * @throws EXISTING_USERNAME 409
     * @return 회원 가입 응답
     */
    @Transactional
    fun registerUser(@Valid request: UserServiceRegisterRequest): UserRegisterResponse {
        val contact = contactRepository.findByEmail(request.email)
            ?: throw CustomException(EMAIL_VALIDATION_INVALID)

        if (!contact.verifyYn)
            throw CustomException(EMAIL_VALIDATION_INVALID)
        if (userRepository.existsByContact(contact))
            throw CustomException(EXISTING_USER)

        validateUsername(UserServiceValidateUsernameRequest(request.username))
        PasswordUtility.validateMatchPassword(request.password, request.passwordReEnter)
        request.password = passwordUtility.encryptPassword(request.password)

        val user = userRepository.save(request.toUserEntity(contact))
        userRoleRepository.save(request.toUserRoleEntity(user))

        return UserRegisterResponse(user)
    }

    /**
     * 아이디 찾기
     * @param request 회원 아이디 찾기 요청
     * @throws USER_NOT_FOUND 404
     * @throws EMAIL_SEND_ERROR 500
     */
    fun findUsername(@Valid request: UserServiceFindUsernameRequest) {
        val user = userRepository.findFetchContact(request.email, null, null)
            ?: throw CustomException(USER_NOT_FOUND)

        emailService.sendEmail(user.contact.email, user.username, FORGOT_USERNAME)
    }

    /**
     * 비밀번호 찾기
     * @param request 회원 비밀번호 찾기 요청
     * @throws USER_NOT_FOUND 404
     * @throws EMAIL_SEND_ERROR 500
     */
    @Transactional
    fun findPassword(request: UserServiceFindPasswordRequest) {
        val user = userRepository.findFetchContact(request.email, request.username, null)
            ?: throw CustomException(USER_NOT_FOUND)

        val password = PasswordUtility.generateRandomCode(8)
        val encryptedPassword = passwordUtility.encryptPassword(password)
        user.resetPassword(encryptedPassword)

        emailService.sendEmail(user.contact.email, password, FORGOT_PASSWORD)
    }

    /**
     * 로그인
     * @param request 회원 로그인 요청
     * @throws LOGIN_UNAUTHENTICATED 401
     * @return 회원 로그인 응답
     */
    fun login(request: UserServiceLoginRequest): UserLoginResponse {
        val user = userRepository.findByUsername(request.username) ?: throw CustomException(LOGIN_UNAUTHENTICATED)

        passwordUtility.verifyPassword(request.password, user.password)

        return UserLoginResponse(user)
    }

    /**
     * 비밀번호 변경
     * @param userId 회원 식별자
     * @param request 비밀번호 변경 요청
     * @throws PASSWORD_MATCH_INVALID 400
     * @throws LOGIN_UNAUTHENTICATED 401
     */
    @Transactional
    fun changePassword(userId: Long, @Valid request: UserServiceChangePasswordRequest) {
        PasswordUtility.validateMatchPassword(request.newPassword, request.newPasswordReEnter)

        val user = userRepository.find(userId) ?: throw CustomException(LOGIN_UNAUTHENTICATED)
        passwordUtility.verifyPassword(request.password, user.password)
        val encryptedPassword = passwordUtility.encryptPassword(request.newPassword)

        user.changePassword(encryptedPassword)
    }
}