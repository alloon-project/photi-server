package com.photi.apis.enduser.controller.auth

import com.photi.apis.enduser.common.exception.GlobalApiErrorResponses
import com.photi.apis.enduser.common.exception.UserApiErrorResponses
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.config.security.AuthUser
import com.photi.apis.enduser.config.security.CustomUserDetails
import com.photi.apis.enduser.config.security.JwtAuthenticationFilter.Companion.BEARER
import com.photi.apis.enduser.config.security.JwtTokenProvider
import com.photi.apis.enduser.config.security.JwtTokenProvider.Companion.AUTHORIZATION_HEADER
import com.photi.apis.enduser.config.security.JwtTokenProvider.Companion.REFRESH_TOKEN_HEADER
import com.photi.apis.enduser.config.security.getUserId
import com.photi.apis.enduser.controller.auth.dto.request.*
import com.photi.apis.enduser.controller.auth.dto.response.FindWithdrawDateResponse
import com.photi.apis.enduser.controller.auth.dto.response.LoginResponse
import com.photi.apis.enduser.controller.auth.dto.response.SignUpResponse
import com.photi.core.domain.common.consts.RegexPattern.LOWERCASE_NUMBER_UNDERSCORE
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.consts.SwaggerKey.REFRESH_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.*
import com.photi.core.domain.user.exception.UserErrorCode.*
import com.photi.core.domain.user.model.RoleType
import com.photi.core.domain.user.model.RoleType.Companion.getRole
import com.photi.core.domain.user.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v2/auth")
@Tag(name = "Auth", description = "인증 API")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @PostMapping("/code")
    @Operation(summary = "이메일 인증코드 전송")
    @ApiResponse(responseCode = "201")
    @UserApiErrorResponses([EXISTING_EMAIL, DELETED_USER])
    fun sendEmailAuthenticationCode(@RequestBody @Valid request: SendEmailAuthenticationCodeRequest): ResponseEntity<StringSuccessResponse> {
        authService.sendEmailAuthenticationCode(request.toServiceDto())
        return ResponseEntity.status(CREATED)
            .body(StringSuccessResponse("이메일 인증코드를 보냈습니다."))
    }

    @PatchMapping("/code")
    @Operation(summary = "이메일 인증코드 검증")
    @ApiResponse(responseCode = "200")
    @UserApiErrorResponses([EMAIL_VERIFICATION_CODE_INVALID, EMAIL_NOT_FOUND])
    fun validateEmailAuthenticationCode(@RequestBody @Valid request: ValidateEmailAuthenticationCodeRequest): ResponseEntity<StringSuccessResponse> {
        authService.validateEmailAuthenticationCode(request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("이메일 인증코드가 검증 되었습니다."))
    }

    @GetMapping("/validate/name")
    @Operation(summary = "아이디 검증")
    @ApiResponse(responseCode = "200")
    @UserApiErrorResponses([USERNAME_FORMAT_INVALID, UNAVAILABLE_USERNAME, EXISTING_USERNAME])
    fun validateUsername(
        @RequestParam @NotBlank @Size(min = 5, max = 20)
        @Pattern(regexp = LOWERCASE_NUMBER_UNDERSCORE)
        @Parameter(description = "아이디", example = "photi_123")
        username: String,
    ): ResponseEntity<StringSuccessResponse> {
        authService.validateUsername(username)
        return ResponseEntity.ok(StringSuccessResponse("사용 가능한 아이디입니다."))
    }

    @PostMapping
    @Operation(summary = "회원가입")
    @ApiResponse(responseCode = "201")
    @UserApiErrorResponses([EMAIL_NOT_FOUND, EXISTING_USER, UNAVAILABLE_USERNAME, EXISTING_USERNAME])
    fun signUp(@RequestBody @Valid request: SignUpRequest): ResponseEntity<SignUpResponse> {
        val signUpUser = authService.signUp(request.toServiceDto())
        val response = SignUpResponse.of(signUpUser)
        val headers = jwtTokenProvider.createToken(response.userId, RoleType.USER)
        return ResponseEntity.status(CREATED).headers(headers).body(response)
    }

    @PostMapping("/username")
    @Operation(summary = "아이디 찾기")
    @ApiResponse(responseCode = "200")
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun findUsername(@RequestBody @Valid request: FindUsernameRequest): ResponseEntity<StringSuccessResponse> {
        authService.findUsername(request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("아이디를 이메일로 전송했습니다."))
    }

    @PostMapping("/password")
    @Operation(summary = "비밀번호 찾기")
    @ApiResponse(responseCode = "200")
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun findPassword(@RequestBody @Valid request: FindPasswordRequest): ResponseEntity<StringSuccessResponse> {
        authService.findPassword(request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("임시 비밀번호를 이메일로 전송했습니다."))
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    @ApiResponse(responseCode = "200")
    @UserApiErrorResponses([LOGIN_UNAUTHENTICATED, DELETED_USER])
    fun login(@RequestBody @Valid request: LoginRequest): ResponseEntity<LoginResponse> {
        val loginUser = authService.login(request.toServiceDto())
        val response = LoginResponse.of(loginUser)
        val headers = jwtTokenProvider.createToken(response.userId, getRole(response.username))
        return ResponseEntity.status(OK).headers(headers).body(response)
    }

    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([PASSWORD_MATCH_INVALID, LOGIN_UNAUTHENTICATED])
    fun changePassword(
        @AuthUser user: CustomUserDetails,
        @RequestBody @Valid request: ChangePasswordRequest,
    ): ResponseEntity<StringSuccessResponse> {
        authService.changePassword(user.getUserId(), request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("비밀번호가 변경되었습니다."))
    }

    @PatchMapping
    @Operation(summary = "회원 탈퇴", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([USER_NOT_FOUND, LOGIN_UNAUTHENTICATED])
    fun withdraw(
        @AuthUser user: CustomUserDetails,
        @RequestBody @Valid request: WithdrawRequest,
    ): ResponseEntity<StringSuccessResponse> {
        authService.withdraw(user.getUserId(), request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("회원 탈퇴가 완료되었습니다."))
    }

    @PostMapping("/deleted-date")
    @Operation(summary = "회원 탈퇴 날짜 조회")
    @ApiResponse(responseCode = "200")
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun findWithdrawDate(@RequestBody @Valid request: FindWithdrawDateRequest): ResponseEntity<FindWithdrawDateResponse> {
        val deletedDate = authService.findWithdrawDate(request.toServiceDto())
        val response = FindWithdrawDateResponse.of(deletedDate)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/validate/token")
    @Operation(summary = "액세스 토큰 유효성 검증", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun validateAccessToken(request: HttpServletRequest): ResponseEntity<StringSuccessResponse> {
        val accessToken = request.getHeader(AUTHORIZATION_HEADER).removePrefix(BEARER)
        jwtTokenProvider.parseClaims(accessToken)
        return ResponseEntity.status(OK).body(StringSuccessResponse("유효한 액세스 토큰입니다."))
    }

    @PostMapping("/token")
    @Operation(summary = "토큰 재발급", security = [SecurityRequirement(name = REFRESH_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun refreshToken(request: HttpServletRequest): ResponseEntity<StringSuccessResponse> {
        val refreshToken = request.getHeader(REFRESH_TOKEN_HEADER)
        val userId = jwtTokenProvider.getUserIdBy(refreshToken)
        val headers = jwtTokenProvider.createToken(userId, RoleType.USER)
        return ResponseEntity.status(OK)
            .headers(headers)
            .body(StringSuccessResponse("토큰이 재발급 됐습니다."))
    }
}
