package com.photi.core.domain.user.service

import com.photi.core.domain.user.dto.LoginDto
import com.photi.core.domain.user.dto.OAuthSignUpDto
import com.photi.core.domain.user.dto.OidcPayload
import com.photi.core.domain.user.dto.SignUpDto
import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.user.model.OAuthProviderType
import com.photi.core.domain.user.port.OAuthFactoryPort
import com.photi.core.domain.user.port.OAuthPort
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
    fun signUp(provider: OAuthProviderType, idToken: String, dto: OAuthSignUpDto): SignUpDto {
        val oAuthPort = oAuthFactory.getOAuthAdapter(provider)
        val idTokenPayload = getOidcPayload(idToken, oAuthPort)
        val oAuthInfo = oAuthPort.createOAuthInfo(idTokenPayload.sub)
        if (userQueryService.existsBy(oAuthInfo)) throw UserException.ExistsUserException()
        val user = userCommandService.createUser(
            dto,
            oAuthInfo,
            idTokenPayload.email,
            idTokenPayload.image,
        )
        return SignUpDto.of(user)
    }

    fun login(provider: OAuthProviderType, idToken: String): LoginDto {
        val oAuthPort = oAuthFactory.getOAuthAdapter(provider)
        val idTokenPayload = getOidcPayload(idToken, oAuthPort)
        val oAuthInfo = oAuthPort.createOAuthInfo(idTokenPayload.sub)
        val user = userQueryService.getLoginUserBy(oAuthInfo)
            ?: throw UserException.NotFoundUserException()
        user.login(userValidator)
        return LoginDto.of(user)
    }

    private fun getOidcPayload(idToken: String, oAuthPort: OAuthPort): OidcPayload {
        val properties = oAuthPort.getProperties()
        return oAuthPort.getIdTokenPayload(
            idToken,
            properties.baseUrl,
            properties.restApiKey,
            "nonce", // todo '인가 코드 요청 api' 요청 시 전달한 nonce 값과 동일한 값
        )
    }
}
