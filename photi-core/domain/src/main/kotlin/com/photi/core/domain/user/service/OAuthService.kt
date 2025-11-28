package com.photi.core.domain.user.service

import com.photi.core.domain.common.properties.KakaoOAuthProperties
import com.photi.core.domain.user.dto.LoginDto
import com.photi.core.domain.user.dto.OAuthSignUpDto
import com.photi.core.domain.user.dto.SignUpDto
import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.user.model.OAuthInfo
import com.photi.core.domain.user.port.OAuthPort
import com.photi.core.domain.user.service.command.UserCommandService
import com.photi.core.domain.user.service.query.UserQueryService
import com.photi.core.domain.user.validator.UserValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OAuthService(
    private val oAuthPort: OAuthPort,
    private val kakaoOAuthProperties: KakaoOAuthProperties,
    private val userCommandService: UserCommandService,
    private val userQueryService: UserQueryService,
    private val userValidator: UserValidator,
) {

    @Transactional
    fun kakaoSignUp(idToken: String, dto: OAuthSignUpDto): SignUpDto {
        val idTokenPayload = getOidcPayload(idToken)
        val oAuthInfo = OAuthInfo.ofKakao(idTokenPayload.sub)
        if (userQueryService.existsBy(oAuthInfo)) throw UserException.ExistsUserException()
        val user = userCommandService.createUser(dto, oAuthInfo, idTokenPayload.email)
        return SignUpDto.of(user)
    }

    fun kakaoLogin(idToken: String): LoginDto {
        val idTokenPayload = getOidcPayload(idToken)
        val oAuthInfo = OAuthInfo.ofKakao(idTokenPayload.sub)
        val user = userQueryService.getLoginUserBy(oAuthInfo)
            ?: throw UserException.NotFoundUserException()
        user.login(userValidator)
        return LoginDto.of(user)
    }

    private fun getOidcPayload(idToken: String) = oAuthPort.getIdTokenPayload(
        idToken,
        kakaoOAuthProperties.baseUrl,
        kakaoOAuthProperties.restApiKey,
        "nonce", // todo '인가 코드 요청 api' 요청 시 전달한 nonce 값과 동일한 값
    )
}
