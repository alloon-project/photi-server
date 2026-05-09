package com.photi.core.domain.user.service

import com.photi.core.domain.user.dto.OAuthLoginDto
import com.photi.core.domain.user.dto.OAuthUpdateUsernameDto
import com.photi.core.domain.user.dto.OAuthWithdrawDto
import com.photi.core.domain.user.dto.OidcPayload
import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.user.model.OAuthInfo
import com.photi.core.domain.user.model.OAuthProviderType
import com.photi.core.domain.user.port.OAuthFactoryPort
import com.photi.core.domain.user.service.command.UserCommandService
import com.photi.core.domain.user.service.query.UserQueryService
import com.photi.core.domain.user.validator.UserValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OAuthService(
    private val oAuthFactory: OAuthFactoryPort,
    private val userCommandService: UserCommandService,
    private val userQueryService: UserQueryService,
    private val userValidator: UserValidator,
) {

    @Transactional
    fun login(provider: OAuthProviderType, idToken: String): OAuthLoginDto {
        val oAuthPort = oAuthFactory.getOAuthAdapter(provider)
        val idTokenPayload = oAuthPort.getIdTokenPayload(idToken)
        val oAuthInfo = oAuthPort.createOAuthInfo(idTokenPayload.sub)
        val user =
            userQueryService.getLoginUserBy(oAuthInfo) ?: return newUser(oAuthInfo, idTokenPayload)
        user.login(userValidator)
        return OAuthLoginDto.of(user)
    }

    @Transactional
    fun updateUsername(id: Long, dto: OAuthUpdateUsernameDto) {
        val user = userQueryService.getUserBy(id)
            .orElseThrow { throw UserException.NotFoundUserException() }
        user.changeUsername(dto.username)
    }

    @Transactional
    fun withdraw(dto: OAuthWithdrawDto) {
        val oAuthInfo = OAuthInfo(dto.provider, dto.sub)
        val user = userQueryService.getLoginUserBy(oAuthInfo)
            ?: throw UserException.NotFoundUserException()
        user.oAuthWithdraw()
    }

    @Transactional
    fun appleWithdraw(id: Long, accessToken: String) {
        val user = userQueryService.getUserBy(id)
            .orElseThrow { throw UserException.NotFoundUserException() }
        val oAuthPort = oAuthFactory.getOAuthAdapter(OAuthProviderType.APPLE)
        oAuthPort.withdraw(accessToken)
        user.oAuthWithdraw()
    }

    private fun newUser(oAuthInfo: OAuthInfo, idTokenPayload: OidcPayload) =
        userCommandService.createUser(
            oAuthInfo,
            idTokenPayload.email,
            idTokenPayload.image,
        ).run {
            OAuthLoginDto.of(this)
        }
}
