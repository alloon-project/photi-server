package com.photi.apis.enduser.controller.auth

import com.photi.apis.enduser.common.exception.ApiErrorResponses
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.controller.auth.dto.request.*
import com.photi.apis.enduser.controller.auth.dto.response.FindWithdrawDateResponse
import com.photi.apis.enduser.controller.auth.dto.response.LoginResponse
import com.photi.apis.enduser.controller.auth.dto.response.SignUpResponse
import com.photi.core.domain.common.consts.CustomHttpHeaders
import com.photi.core.domain.common.consts.RegexPatternConstants.LOWERCASE_NUMBER_UNDERSCORE
import com.photi.core.domain.common.consts.SwaggerConstants.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.consts.SwaggerConstants.REFRESH_TOKEN_KEY
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.user.usecase.AuthService
import com.photi.apis.enduser.config.security.JwtProvider
import com.photi.utils.UserUtil
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
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Validated
@RestController
@Tag(name = "Auth", description = "인증 API")
class AuthController(
    private val authService: AuthService,
    private val jwtProvider: JwtProvider,
) {

    @PostMapping("/api/contacts")
    @Operation(summary = "이메일 인증코드 전송")
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([ExceptionCode.EXISTING_EMAIL, ExceptionCode.DELETED_USER])
    fun sendEmailAuthenticationCode(
        @RequestBody @Valid request: SendEmailAuthenticationCodeRequest,
    ): ResponseEntity<StringSuccessResponse> {
        authService.sendEmailAuthenticationCode(request.toServiceDto())
        return ResponseEntity.status(CREATED)
            .body(StringSuccessResponse("이메일 인증코드를 보냈습니다."))
    }

    @PatchMapping("/api/contacts/verify")
    @Operation(summary = "이메일 인증코드 검증")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.EMAIL_VERIFICATION_CODE_INVALID, ExceptionCode.EMAIL_NOT_FOUND])
    fun validateEmailAuthenticationCode(
        @RequestBody @Valid request: ValidateEmailAuthenticationCodeRequest,
    ): ResponseEntity<StringSuccessResponse> {
        authService.validateEmailAuthenticationCode(request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("이메일 인증코드가 검증 되었습니다."))
    }

    @GetMapping("/api/users/username")
    @Operation(summary = "아이디 검증")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.USERNAME_FORMAT_INVALID, ExceptionCode.UNAVAILABLE_USERNAME, ExceptionCode.EXISTING_USERNAME])
    fun validateUsername(
        @RequestParam @NotBlank @Size(min = 5, max = 20)
        @Pattern(regexp = LOWERCASE_NUMBER_UNDERSCORE)
        @Parameter(description = "아이디", example = "photi_123")
        username: String,
    ): ResponseEntity<StringSuccessResponse> {
        authService.validateUsername(username)
        return ResponseEntity.ok(StringSuccessResponse("사용 가능한 아이디입니다."))
    }

    @PostMapping("/api/users/register")
    @Operation(summary = "회원가입")
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([ExceptionCode.EMAIL_VALIDATION_INVALID, ExceptionCode.PASSWORD_MATCH_INVALID, ExceptionCode.EXISTING_USER, ExceptionCode.UNAVAILABLE_USERNAME, ExceptionCode.EXISTING_USERNAME])
    fun signUp(
        @RequestBody @Valid request: SignUpRequest,
    ): ResponseEntity<SignUpResponse> {
        val signUpUser = authService.signUp(request.toServiceDto())
        val response = SignUpResponse.of(signUpUser)
        val headers = jwtProvider.createToken(response.userId)
        return ResponseEntity.status(CREATED).headers(headers).body(response)
    }

    @PostMapping("/api/users/find-username")
    @Operation(summary = "아이디 찾기")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.USER_NOT_FOUND])
    fun findUsername(
        @RequestBody @Valid request: FindUsernameRequest,
    ): ResponseEntity<StringSuccessResponse> {
        authService.findUsername(request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("아이디를 이메일로 전송했습니다."))
    }

    @PostMapping("/api/users/find-password")
    @Operation(summary = "비밀번호 찾기")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.USER_NOT_FOUND])
    fun findPassword(
        @RequestBody @Valid request: FindPasswordRequest,
    ): ResponseEntity<StringSuccessResponse> {
        authService.findPassword(request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("임시 비밀번호를 이메일로 전송했습니다."))
    }

    @PostMapping("/api/users/login")
    @Operation(summary = "로그인")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.LOGIN_UNAUTHENTICATED, ExceptionCode.DELETED_USER])
    fun login(
        @RequestBody @Valid request: LoginRequest,
    ): ResponseEntity<LoginResponse> {
        val loginUser = authService.login(request.toServiceDto())
        val response = LoginResponse.of(loginUser)
        val headers = jwtProvider.createToken(response.userId)
        return ResponseEntity.status(OK).headers(headers).body(response)
    }

    @PatchMapping("/api/users/password")
    @Operation(summary = "비밀번호 변경", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.PASSWORD_MATCH_INVALID, ExceptionCode.LOGIN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED])
    fun changePassword(
        principal: Principal,
        @RequestBody @Valid request: ChangePasswordRequest,
    ): ResponseEntity<StringSuccessResponse> {
        authService.changePassword(UserUtil.getUserId(principal), request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("비밀번호가 변경되었습니다."))
    }

    @PatchMapping("/api/users")
    @Operation(summary = "회원 탈퇴", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.USER_NOT_FOUND, ExceptionCode.LOGIN_UNAUTHENTICATED])
    fun deleteUser(
        principal: Principal,
        @RequestBody @Valid request: WithdrawRequest,
    ): ResponseEntity<StringSuccessResponse> {
        authService.withdraw(UserUtil.getUserId(principal), request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("회원 탈퇴가 완료되었습니다."))
    }

    @PostMapping("/api/users/deleted-date")
    @Operation(summary = "회원 탈퇴 날짜 조회")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.USER_NOT_FOUND])
    fun findWithdrawDate(
        @RequestBody @Valid request: FindWithdrawDateRequest,
    ): ResponseEntity<FindWithdrawDateResponse> {
        val deletedDate = authService.findWithdrawDate(request.toServiceDto())
        val response = FindWithdrawDateResponse.of(deletedDate)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/api/auth/validate/access-token")
    @Operation(summary = "액세스 토큰 유효성 검증", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED])
    fun validateAccessToken(
        request: HttpServletRequest,
    ): ResponseEntity<StringSuccessResponse> {
        val accessToken = request.getHeader(HttpHeaders.AUTHORIZATION)
        jwtProvider.validateAccessTokenAndSetAuthentication(accessToken)
        return ResponseEntity.status(OK).body(StringSuccessResponse("유효한 액세스 토큰입니다."))
    }

    @PostMapping("/api/users/token")
    @Operation(summary = "토큰 재발급", security = [SecurityRequirement(name = REFRESH_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED])
    fun refreshToken(
        request: HttpServletRequest,
    ): ResponseEntity<StringSuccessResponse> {
        val refreshToken = request.getHeader(CustomHttpHeaders.REFRESH_TOKEN)
        val userId = jwtProvider.getUserId(refreshToken)
        val headers = jwtProvider.createToken(userId)
        return ResponseEntity.status(OK)
            .headers(headers)
            .body(StringSuccessResponse("토큰이 재발급 됐습니다."))
    }
}
