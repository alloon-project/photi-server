package com.photi.apis.enduser.controller.oauth

import com.photi.apis.enduser.common.exception.GlobalApiErrorResponses
import com.photi.apis.enduser.common.exception.UserApiErrorResponses
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.config.security.AuthUser
import com.photi.apis.enduser.config.security.CustomUserDetails
import com.photi.apis.enduser.config.security.JwtTokenProvider
import com.photi.apis.enduser.config.security.getUserId
import com.photi.apis.enduser.controller.oauth.request.OAuthUpdateUsernameRequest
import com.photi.apis.enduser.controller.oauth.response.OAuthLoginResponse
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.*
import com.photi.core.domain.user.exception.UserErrorCode.*
import com.photi.core.domain.user.model.OAuthProviderType
import com.photi.core.domain.user.model.RoleType
import com.photi.core.domain.user.service.OAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
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

    @GetMapping("/{provider}/login")
    @Operation(summary = "OAuth 로그인")
    @ApiResponse(responseCode = "200")
    @UserApiErrorResponses([DELETED_USER])
    @GlobalApiErrorResponses([INVALID_TOKEN, EXPIRED_TOKEN])
    fun login(
        @PathVariable provider: OAuthProviderType,
        @RequestParam("id_token") @Parameter(description = "ID 토큰") idToken: String,
    ): ResponseEntity<OAuthLoginResponse> {
        val loginUser = oAuthService.login(provider, idToken)
        val response = OAuthLoginResponse.of(loginUser)
        val headers = jwtTokenProvider.createToken(loginUser.userId, RoleType.USER)
        return ResponseEntity.status(OK).headers(headers).body(response)
    }

    @PostMapping("/username")
    @Operation(summary = "OAuth 아이디 설정", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @UserApiErrorResponses([USER_NOT_FOUND])
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, INVALID_TOKEN, EXPIRED_TOKEN])
    fun updateUsername(
        @AuthUser user: CustomUserDetails,
        @RequestBody @Valid request: OAuthUpdateUsernameRequest,
    ): ResponseEntity<StringSuccessResponse> {
        oAuthService.updateUsername(user.getUserId(), request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("OAuth 아이디 설정이 완료되었습니다."))
    }

    @PatchMapping("/{provider}/withdraw")
    @Operation(summary = "OAuth 회원 탈퇴", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun withdraw(
        @AuthUser user: CustomUserDetails,
        @PathVariable provider: OAuthProviderType,
        @RequestParam("access_token") @Parameter(description = "OAuth 액세스 토큰(포티 jwt의 액세스 토큰이 아닌, 각 oauth에서 발급받은 액세스 토큰입니다!)") accessToken: String,
    ): ResponseEntity<StringSuccessResponse> {
        oAuthService.withdraw(user.getUserId(), provider, accessToken)
        return ResponseEntity.ok(StringSuccessResponse("OAuth 회원 탈퇴가 완료되었습니다."))
    }
}
