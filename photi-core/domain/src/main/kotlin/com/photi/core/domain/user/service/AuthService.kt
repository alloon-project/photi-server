package com.photi.core.domain.user.service

import com.photi.core.domain.user.dto.*
import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.user.port.PasswordPort
import com.photi.core.domain.user.port.email.EmailMessage
import com.photi.core.domain.user.port.email.EmailPort
import com.photi.core.domain.user.service.command.UserCommandService
import com.photi.core.domain.user.service.query.UserQueryService
import com.photi.core.domain.user.validator.UserValidator
import com.photi.utils.CodeUtil.getAuthenticationCode
import com.photi.utils.PasswordUtil.getTemporaryPassword
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
@ConditionalOnBean(PasswordPort::class)
class AuthService(
    private val userValidator: UserValidator,
    private val userQueryService: UserQueryService,
    private val userCommandService: UserCommandService,
    private val passwordPort: PasswordPort,
    private val emailPort: EmailPort,
) {

    @Transactional
    fun sendEmailAuthenticationCode(dto: SendEmailAuthenticationCodeDto) {
        val authenticationCode = getAuthenticationCode()
        userQueryService.getUnAuthenticatedUserBy(dto.email)
            ?.issueNewAuthenticationCode(userValidator, dto.email, authenticationCode)
            ?: userCommandService.createUser(dto, authenticationCode)
        emailPort.send(EmailMessage.SignUpAuthenticationCode(dto.email, authenticationCode))
    }

    @Transactional
    fun validateEmailAuthenticationCode(dto: ValidateEmailAuthenticationCodeDto) {
        userQueryService.getUnAuthenticatedUserBy(dto.email)
            ?.authenticated(userValidator, dto.authenticationCode)
            ?: throw UserException.NotFoundEmailException()
    }

    fun validateUsername(username: String) {
        userValidator.validateUsername(username)
    }

    @Transactional
    fun signUp(dto: SignUpRequestDto): SignUpDto {
        val user = userQueryService.getAuthenticatedUserBy(dto.email)
            ?: throw UserException.NotFoundEmailException()
        user.signUp(userValidator, passwordPort, dto)
        return SignUpDto.of(user)
    }

    fun findUsername(dto: FindUsernameDto) {
        val user = userQueryService.getAuthenticatedUserBy(dto.email)
            ?: throw UserException.NotFoundUserException()
        emailPort.send(EmailMessage.FindUsername(user.email, user.username!!))
    }

    @Transactional
    fun findPassword(dto: FindPasswordDto) {
        val temporaryPassword = getTemporaryPassword()
        userQueryService.getAuthenticatedUserBy(dto.email, dto.username)
            ?.resetPasswordTo(passwordPort, temporaryPassword)
            ?: throw UserException.NotFoundUserException()
        emailPort.send(EmailMessage.FindPassword(dto.email, temporaryPassword))
    }

    fun login(dto: LoginRequestDto): LoginDto {
        val user = userQueryService.getLoginUserBy(dto.username)
            ?: throw UserException.UnauthorizedLoginException()
        userValidator.validatePassword(user, dto.password)
        return LoginDto.of(user)
    }

    @Transactional
    fun changePassword(id: Long, dto: ChangePasswordDto) {
        val user = userQueryService.getUserBy(id)
            .orElseThrow { throw UserException.UnauthorizedLoginException() }
        user.changePasswordTo(userValidator, passwordPort, dto)
    }

    @Transactional
    fun withdraw(id: Long, dto: WithdrawDto) {
        val user = userQueryService.getUserBy(id)
            .orElseThrow { throw UserException.NotFoundUserException() }
        user.withdraw(passwordPort, dto.password)
    }

    fun findWithdrawDate(dto: FindWithdrawDateRequestDto): FindWithdrawDateDto {
        val user = userQueryService.getUserBy(dto.email)
            ?: throw UserException.NotFoundUserException()
        return FindWithdrawDateDto.of(user)
    }
}
