package com.alloon.alloonserver.service.user

import com.alloon.alloonserver.common.constant.EmailConstants.*
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.constant.UnavailableConstants.UNAVAILABLE_USERNAMES
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.domain.user.UserRoleRepository
import com.alloon.alloonserver.domain.user.UserTemplateImageRepository
import com.alloon.alloonserver.service.email.EmailService
import com.alloon.alloonserver.service.user.dto.*
import com.alloon.alloonserver.service.user.response.UserLoginResponse
import com.alloon.alloonserver.service.user.response.UserRegisterResponse
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
    private val userTemplateImageRepository: UserTemplateImageRepository,
    private val emailService: EmailService,
    private val passwordUtility: PasswordUtility,
) {

    @Transactional
    fun sendVerificationCode(@Valid request: ContactServiceSendVerificationDto) {
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

    @Transactional
    fun verifyEmailVerificationCode(@Valid request: ContactServiceVerifyDto) {
        contactRepository.findByEmail(request.email)
            ?.verify(request.verificationCode)
            ?: throw CustomException(EMAIL_NOT_FOUND)
    }

    fun validateUsername(@Valid request: UserServiceValidateUsernameDto) {
        if (request.username in UNAVAILABLE_USERNAMES.fields)
            throw CustomException(UNAVAILABLE_USERNAME)

        if (userRepository.existsByUsername(request.username))
            throw CustomException(EXISTING_USERNAME)
    }

    @Transactional
    fun registerUser(@Valid request: UserServiceRegisterDto): UserRegisterResponse {
        val contact = contactRepository.findByEmail(request.email)
            ?: throw CustomException(EMAIL_VALIDATION_INVALID)

        if (!contact.verifyYn)
            throw CustomException(EMAIL_VALIDATION_INVALID)
        if (userRepository.existsByContact(contact))
            throw CustomException(EXISTING_USER)

        validateUsername(UserServiceValidateUsernameDto(request.username))
        PasswordUtility.validateMatchPassword(request.password, request.passwordReEnter)
        request.password = passwordUtility.encryptPassword(request.password)

        val userTemplateImage = getUserTemplateImage()

        val user = userRepository.save(request.toUserEntity(contact, userTemplateImage))
        userRoleRepository.save(request.toUserRoleEntity(user))

        return UserRegisterResponse(user)
    }

    fun findUsername(@Valid request: UserServiceFindUsernameDto) {
        val user = userRepository.findFetchContact(request.email, null, null)
            ?: throw CustomException(USER_NOT_FOUND)

        emailService.sendEmail(user.contact.email, user.username, FORGOT_USERNAME)
    }

    @Transactional
    fun findPassword(request: UserServiceFindPasswordDto) {
        val user = userRepository.findFetchContact(request.email, request.username, null)
            ?: throw CustomException(USER_NOT_FOUND)

        val password = PasswordUtility.generateRandomCode(8)
        val encryptedPassword = passwordUtility.encryptPassword(password)
        user.resetPassword(encryptedPassword)

        emailService.sendEmail(user.contact.email, password, FORGOT_PASSWORD)
    }

    fun login(request: UserServiceLoginDto): UserLoginResponse {
        val user = userRepository.findByUsername(request.username) ?: throw CustomException(LOGIN_UNAUTHENTICATED)

        passwordUtility.verifyPassword(request.password, user.password)

        return UserLoginResponse(user)
    }

    @Transactional
    fun changePassword(userId: Long, @Valid request: UserServiceChangePasswordDto) {
        PasswordUtility.validateMatchPassword(request.newPassword, request.newPasswordReEnter)

        val user = userRepository.find(userId) ?: throw CustomException(LOGIN_UNAUTHENTICATED)
        passwordUtility.verifyPassword(request.password, user.password)
        val encryptedPassword = passwordUtility.encryptPassword(request.newPassword)

        user.changePassword(encryptedPassword)
    }

    private fun getUserTemplateImage(): String {
        val userTemplateImages = userTemplateImageRepository.findAll()
        return userTemplateImages.randomOrNull()?.imageUrl ?: ""
    }
}