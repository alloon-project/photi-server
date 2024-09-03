package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.user.request.*
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.constant.RegexPatternConstants.Companion.LOWERCASE_NUMBER_UNDERSCORE
import com.alloon.alloonserver.common.response.ApiErrorResponses
import com.alloon.alloonserver.common.response.StringResponse
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.config.SwaggerConfig.Companion.ACCESS_TOKEN_KEY
import com.alloon.alloonserver.config.SwaggerConfig.Companion.REFRESH_TOKEN_KEY
import com.alloon.alloonserver.config.auth.JwtProvider
import com.alloon.alloonserver.service.user.AuthService
import com.alloon.alloonserver.service.user.dto.UserServiceValidateUsernameDto
import com.alloon.alloonserver.service.user.response.UserLoginResponse
import com.alloon.alloonserver.service.user.response.UserRegisterResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
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
    @ApiErrorResponses([EXISTING_EMAIL])
    fun sendVerificationCode(@RequestBody @Valid request: ContactSendVerificationRequest): ResponseEntity<StringResponse> {
        authService.sendVerificationCode(request.toServiceDto())

        return ResponseEntity.status(CREATED)
            .body(StringResponse("이메일 인증코드를 보냈습니다."))
    }

    @PatchMapping("/api/contacts/verify")
    @Operation(summary = "이메일 인증코드 확인")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([EMAIL_VERIFICATION_CODE_INVALID, EMAIL_NOT_FOUND])
    fun verifyEmailVerificationCode(@RequestBody @Valid request: ContactVerifyRequest): ResponseEntity<StringResponse> {
        authService.verifyEmailVerificationCode(request.toServiceDto())

        return ResponseEntity.ok(StringResponse("이메일 인증코드가 확인 되었습니다."))
    }

    @GetMapping("/api/users/username")
    @Operation(summary = "아이디 검증")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([USERNAME_FORMAT_INVALID, UNAVAILABLE_USERNAME, EXISTING_USERNAME])
    fun validateUsername(
        @RequestParam @NotBlank @Size(min = 5, max = 20)
        @Pattern(regexp = LOWERCASE_NUMBER_UNDERSCORE)
        @Parameter(description = "아이디", example = "photi_123")
        username: String
    ): ResponseEntity<StringResponse> {
        authService.validateUsername(UserServiceValidateUsernameDto(username))

        return ResponseEntity.ok(StringResponse("사용 가능한 아이디입니다."))
    }

    @PostMapping("/api/users/register")
    @Operation(summary = "회원가입")
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([EMAIL_VALIDATION_INVALID, PASSWORD_MATCH_INVALID, EXISTING_USER, UNAVAILABLE_USERNAME, EXISTING_USERNAME])
    fun registerUser(@RequestBody @Valid request: UserRegisterRequest): ResponseEntity<UserRegisterResponse> {
        val response = authService.registerUser(request.toServiceDto())
        val headers = jwtProvider.createToken(response.userId)

        return ResponseEntity.status(CREATED).headers(headers).body(response)
    }

    @PostMapping("/api/users/find-username")
    @Operation(summary = "아이디 찾기")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([USER_NOT_FOUND])
    fun findUsername(@RequestBody @Valid request: UserFindUsernameRequest): ResponseEntity<StringResponse> {
        authService.findUsername(request.toServiceDto())

        return ResponseEntity.ok(StringResponse("아이디를 이메일로 전송했습니다."))
    }

    @PostMapping("/api/users/find-password")
    @Operation(summary = "비밀번호 찾기")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([USER_NOT_FOUND])
    fun findPassword(@RequestBody @Valid request: UserFindPasswordRequest): ResponseEntity<StringResponse> {
        authService.findPassword(request.toServiceDto())

        return ResponseEntity.ok(StringResponse("임시 비밀번호를 이메일로 전송했습니다."))
    }

    @PostMapping("/api/users/login")
    @Operation(summary = "로그인")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([LOGIN_UNAUTHENTICATED])
    fun login(@RequestBody @Valid request: UserLoginRequest): ResponseEntity<UserLoginResponse> {
        val response = authService.login(request.toServiceDto())
        val headers = jwtProvider.createToken(response.userId)

        return ResponseEntity.status(OK).headers(headers).body(response)
    }

    @PatchMapping("/api/users/password")
    @Operation(summary = "비밀번호 변경", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([PASSWORD_MATCH_INVALID, LOGIN_UNAUTHENTICATED, TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun changePassword(
        principal: Principal,
        @RequestBody @Valid request: UserChangePasswordRequest
    ): ResponseEntity<StringResponse> {
        authService.changePassword(UserUtility.getUserId(principal), request.toServiceDto())

        return ResponseEntity.ok(StringResponse("비밀번호가 변경되었습니다."))
    }

    @PostMapping("/api/users/token")
    @Operation(summary = "토큰 재발급", security = [SecurityRequirement(name = REFRESH_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun refreshToken(principal: Principal): ResponseEntity<StringResponse> {
        val headers = jwtProvider.createToken(UserUtility.getUserId(principal))

        return ResponseEntity.status(OK)
            .headers(headers)
            .body(StringResponse("토큰이 재발급 됐습니다."))
    }
}