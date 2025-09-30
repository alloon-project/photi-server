package com.photi.core.domain.user.usecase

import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.user.command.UserCommandService
import com.photi.core.domain.user.dto.*
import com.photi.core.domain.user.port.PasswordPort
import com.photi.core.domain.user.port.email.EmailMessage
import com.photi.core.domain.user.port.email.EmailPort
import com.photi.core.domain.user.query.UserQueryService
import com.photi.core.domain.user.validator.UserValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userValidator: UserValidator,
    private val userQueryService: UserQueryService,
    private val userCommandService: UserCommandService,
    private val passwordPort: PasswordPort,
    private val emailPort: EmailPort,
) {

    @Transactional
    fun sendEmailAuthenticationCode(dto: SendEmailAuthenticationCodeDto) {
        val user = userQueryService.getUserBy(dto.email)
        user?.let { user.issueNewAuthenticationCode(userValidator, dto.email) }
            ?: userCommandService.createUser(dto)
        emailPort.send(EmailMessage.SignUpAuthenticationCode(dto.email))
    }

    @Transactional
    fun validateEmailAuthenticationCode(dto: ValidateEmailAuthenticationCodeDto) {
        userQueryService.getUserBy(dto.email)
            ?.authenticated(userValidator, dto.authenticationCode)
    }

    fun validateUsername(username: String) {
        userValidator.validateUsername(username)
    }

    @Transactional
    fun signUp(dto: SignUpRequestDto): SignUpDto {
        val user = userQueryService.getAuthenticatedUserBy(dto.email)
            ?: throw CustomException(ExceptionCode.EMAIL_VALIDATION_INVALID)
        user.signUp(userValidator, passwordPort, dto)
        return SignUpDto.of(user)
    }

    fun findUsername(dto: FindUsernameDto) {
        val user = userQueryService.getAuthenticatedUserBy(dto.email)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
        emailPort.send(EmailMessage.FindUsername(user.email, user.username!!))
    }

    @Transactional
    fun findPassword(dto: FindPasswordDto) {
        val user = userQueryService.getAuthenticatedUserBy(dto.email, dto.username)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
        user.resetPasswordTo(passwordPort)
        emailPort.send(EmailMessage.FindPassword(dto.email))
    }

    fun login(dto: LoginRequestDto): LoginDto {
        val user = userQueryService.getLoginUserBy(dto.username)
            ?: throw CustomException(ExceptionCode.LOGIN_UNAUTHENTICATED)
        userValidator.validatePassword(user, dto.password)
        return LoginDto.of(user)
    }

    @Transactional
    fun changePassword(id: Long, dto: ChangePasswordDto) {
        val user = userQueryService.getUserBy(id)
            .orElseThrow { throw CustomException(ExceptionCode.LOGIN_UNAUTHENTICATED) }
        user.changePasswordTo(userValidator, passwordPort, dto)
    }

    @Transactional
    fun withdraw(id: Long, dto: WithdrawDto) {
        val user = userQueryService.getUserBy(id)
            .orElseThrow { throw CustomException(ExceptionCode.USER_NOT_FOUND) }
        user.withdraw(passwordPort, dto.password)
    }

    fun findWithdrawDate(dto: FindWithdrawDateRequestDto): FindWithdrawDateDto {
        val user = userQueryService.getUserBy(dto.email)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
        return FindWithdrawDateDto.of(user)
    }
}
