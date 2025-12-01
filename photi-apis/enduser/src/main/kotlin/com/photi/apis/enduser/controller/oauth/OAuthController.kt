package com.photi.apis.enduser.controller.oauth

import com.photi.apis.enduser.common.exception.UserApiErrorResponses
import com.photi.apis.enduser.config.security.JwtTokenProvider
import com.photi.apis.enduser.controller.auth.dto.response.LoginResponse
import com.photi.apis.enduser.controller.auth.dto.response.SignUpResponse
import com.photi.apis.enduser.controller.oauth.request.OAuthSignUpRequest
import com.photi.core.domain.user.exception.UserErrorCode.*
import com.photi.core.domain.user.model.OAuthProviderType
import com.photi.core.domain.user.model.RoleType
import com.photi.core.domain.user.model.RoleType.Companion.getRole
import com.photi.core.domain.user.service.OAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v2/oauth")
@Tag(name = "OAuth", description = "OAuth API")
class OAuthController(
    private val oAuthService: OAuthService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @PostMapping("/{provider}/signup")
    @Operation(summary = "OAuth 회원가입")
    @ApiResponse(responseCode = "201")
    @UserApiErrorResponses([EXISTING_USER])
    fun signUp(
        @PathVariable provider: OAuthProviderType,
        @RequestParam("id_token") @Parameter(description = "ID 토큰") idToken: String,
        @RequestBody @Valid request: OAuthSignUpRequest,
    ): ResponseEntity<SignUpResponse> {
        val signUpUser = oAuthService.signUp(provider, idToken, request.toServiceDto())
        val response = SignUpResponse.of(signUpUser)
        val headers = jwtTokenProvider.createToken(response.userId, RoleType.USER)
        return ResponseEntity.status(CREATED).headers(headers).body(response)
    }

    @GetMapping("/{provider}/login")
    @Operation(summary = "OAuth 로그인")
    @ApiResponse(responseCode = "200")
    @UserApiErrorResponses([USER_NOT_FOUND, DELETED_USER])
    fun login(
        @PathVariable provider: OAuthProviderType,
        @RequestParam("id_token") @Parameter(description = "ID 토큰") idToken: String,
    ): ResponseEntity<LoginResponse> {
        val loginUser = oAuthService.login(provider, idToken)
        val response = LoginResponse.of(loginUser)
        val headers = jwtTokenProvider.createToken(response.userId, getRole(response.username))
        return ResponseEntity.status(OK).headers(headers).body(response)
    }
}
