package com.photi.core.domain.user.usecase

import com.photi.core.domain.common.PasswordUtility
import com.photi.core.domain.common.consts.EmailConstants
import com.photi.core.domain.common.consts.UnavailableConstants
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.email.usecase.EmailService
import com.photi.core.domain.user.dto.*
import com.photi.core.domain.user.model.repository.ContactRepository
import com.photi.core.domain.user.model.repository.UserRepository
import com.photi.core.domain.user.model.repository.UserRoleRepository
import com.photi.core.domain.user.model.repository.UserTemplateImageRepository
import com.photi.utils.CodeUtil.getVerificationCode
import com.photi.utils.PasswordUtil.getTemporaryPassword
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
    private val passwordUtility: PasswordUtility,
    private val emailService: EmailService,
) {

    @Transactional
    fun sendVerificationCode(dto: ContactServiceSendVerificationDto) {
        val verificationCode = getVerificationCode()
        val contact = contactRepository.findByEmail(dto.email)

        contact?.let {
            if (it.isDeleted) {
                throw CustomException(ExceptionCode.DELETED_USER)
            }
            if (userRepository.existsByContactAndIsDeletedFalse(it)) {
                throw CustomException(ExceptionCode.EXISTING_EMAIL)
            }
            it.changeVerificationCode(verificationCode)
        } ?: run {
            contactRepository.save(dto.toEntity(verificationCode))
        }

        emailService.sendEmail(
            dto.email,
            verificationCode,
            EmailConstants.REGISTER_VERIFICATION_CODE,
        )
    }

    @Transactional
    fun verifyEmailVerificationCode(dto: ContactServiceVerifyDto) {
        contactRepository.findByEmail(dto.email)
            ?.verify(dto.verificationCode)
            ?: throw CustomException(ExceptionCode.EMAIL_NOT_FOUND)
    }

    fun validateUsername(dto: UserServiceValidateUsernameDto) {
        if (dto.username in UnavailableConstants.UNAVAILABLE_USERNAMES.fields) {
            throw CustomException(ExceptionCode.UNAVAILABLE_USERNAME)
        }
        if (userRepository.existsByUsername(dto.username)) {
            throw CustomException(ExceptionCode.EXISTING_USERNAME)
        }
    }

    @Transactional
    fun registerUser(dto: UserServiceRegisterDto): UserRegisterDto {
        val contact = contactRepository.findByEmail(dto.email)
            ?: throw CustomException(ExceptionCode.EMAIL_VALIDATION_INVALID)

        if (!contact.verifyYn) {
            throw CustomException(ExceptionCode.EMAIL_VALIDATION_INVALID)
        }
        if (userRepository.existsByContactAndIsDeletedFalse(contact)) {
            throw CustomException(ExceptionCode.EXISTING_USER)
        }

        validateUsername(UserServiceValidateUsernameDto(dto.username))
        dto.password = passwordUtility.encryptPassword(dto.password)

        val userTemplateImage = getUserTemplateImage()

        val user = userRepository.save(dto.toUserEntity(contact, userTemplateImage))
        userRoleRepository.save(dto.toUserRoleEntity(user))

        return UserRegisterDto.of(user)
    }

    fun findUsername(dto: UserServiceFindUsernameDto) {
        val user = userRepository.findFetchContact(dto.email, null, null)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)

        emailService.sendEmail(user.contact.email, user.username, EmailConstants.FORGOT_USERNAME)
    }

    @Transactional
    fun findPassword(dto: UserServiceFindPasswordDto) {
        val user = userRepository.findFetchContact(dto.email, dto.username, null)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)

        val password = getTemporaryPassword(8)
        val encryptedPassword = passwordUtility.encryptPassword(password)
        user.resetPassword(encryptedPassword)

        emailService.sendEmail(user.contact.email, password, EmailConstants.FORGOT_PASSWORD)
    }

    fun login(dto: UserServiceLoginDto): UserLoginDto {
        val user = userRepository.findByUsername(dto.username)
            ?: throw CustomException(ExceptionCode.LOGIN_UNAUTHENTICATED)

        if (user.contact.isDeleted) {
            throw CustomException(ExceptionCode.DELETED_USER)
        }
        passwordUtility.verifyPassword(dto.password, user.password)
        return UserLoginDto.of(user)
    }

    @Transactional
    fun changePassword(userId: Long, dto: UserServiceChangePasswordDto) {
        validateMatchPassword(dto.newPassword, dto.newPasswordReEnter)

        val user = userRepository.find(userId)
            ?: throw CustomException(ExceptionCode.LOGIN_UNAUTHENTICATED)
        passwordUtility.verifyPassword(dto.password, user.password)
        val encryptedPassword = passwordUtility.encryptPassword(dto.newPassword)

        user.changePassword(encryptedPassword)
    }

    @Transactional
    fun deleteUser(userId: Long, dto: DeleteUserDto) {
        val user = userRepository.find(userId)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)

        passwordUtility.verifyPassword(dto.password, user.password)

        user.contact.softDelete()
        user.softDelete()
    }

    fun findUserDeletedDate(dto: UserDeletedDateDto): FindUserDeletedDateDto {
        val contact = contactRepository.findByEmail(dto.email)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
        return FindUserDeletedDateDto(contact.deletedDate)
    }

    private fun getUserTemplateImage(): String {
        val userTemplateImages = userTemplateImageRepository.findAll()
        return userTemplateImages.randomOrNull()?.imageUrl ?: ""
    }

    private fun validateMatchPassword(password: String, passwordReEnter: String) {
        if (password != passwordReEnter) {
            throw CustomException(ExceptionCode.PASSWORD_MATCH_INVALID)
        }
    }
}
