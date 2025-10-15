package com.photi.core.domain.user.service

import com.photi.core.domain.user.dto.*
import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.user.port.PasswordPort
import com.photi.core.domain.user.port.email.EmailMessage
import com.photi.core.domain.user.port.email.EmailPort
import com.photi.core.domain.user.service.command.UserCommandService
import com.photi.core.domain.user.service.query.UserQueryService
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
            ?: throw UserException.InvalidEmailException()
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
        val user = userQueryService.getAuthenticatedUserBy(dto.email, dto.username)
            ?: throw UserException.NotFoundUserException()
        user.resetPasswordTo(passwordPort)
        emailPort.send(EmailMessage.FindPassword(dto.email))
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
