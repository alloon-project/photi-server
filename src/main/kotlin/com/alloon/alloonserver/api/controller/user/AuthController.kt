package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.user.request.*
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.api.service.user.request.*
import com.alloon.alloonserver.api.service.user.response.UserLoginResponse
import com.alloon.alloonserver.api.service.user.response.UserRegisterResponse
import com.alloon.alloonserver.common.constant.SuccessCode.*
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.config.auth.JwtProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
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
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "이메일 인증코드 전송 성공")])
    fun sendVerificationCode(
        @RequestBody @Valid @Schema(implementation = ContactServiceSendVerificationRequest::class)
        request: ContactSendVerificationRequest
    ): ResponseEntity<DefaultResponse> {
        authService.sendVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_SENT)
    }

    @PatchMapping("/api/contacts/verify")
    @Operation(summary = "이메일 인증코드 확인")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "이메일 인증코드 확인 성공")])
    fun verifyEmailVerificationCode(
        @RequestBody @Valid @Schema(implementation = ContactServiceVerifyRequest::class)
        request: ContactVerifyRequest
    ): ResponseEntity<DefaultResponse> {
        authService.verifyEmailVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_VERIFIED)
    }

    @GetMapping("/api/users/username")
    @Operation(summary = "아이디 검증")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "아이디 검증 성공")])
    fun validateUsername(
        @RequestParam("username") @NotBlank(message = "아이디는 필수 입력입니다.")
        @Parameter(description = "아이디", example = "photi")
        username: String
    ): ResponseEntity<DefaultResponse> {
        authService.validateUsername(UserServiceValidateUsernameRequest(username))

        return DefaultResponse.toResponseEntity(USERNAME_AVAILABLE)
    }

    @PostMapping("/api/users/register")
    @Operation(summary = "회원가입")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "회원가입 성공",
                content = [Content(schema = Schema(implementation = UserRegisterResponse::class))]
            )
        ]
    )
    fun registerUser(
        @RequestBody @Valid @Schema(implementation = UserServiceRegisterRequest::class)
        request: UserRegisterRequest
    ): ResponseEntity<DefaultSingleResponse> {
        val response = authService.registerUser(request.toServiceRequest())

        val headers = jwtProvider.createToken(response.userId)

        return DefaultSingleResponse.toResponseEntity(headers, USER_REGISTERED, response)
    }

    @PostMapping("/api/users/find-username")
    @Operation(summary = "아이디 찾기")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "아이디 찾기 성공")])
    fun findUsername(
        @RequestBody @Valid @Schema(implementation = UserServiceFindUsernameRequest::class)
        request: UserFindUsernameRequest
    ): ResponseEntity<DefaultResponse> {
        authService.findUsername(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(USERNAME_SENT)
    }

    @PostMapping("/api/users/find-password")
    @Operation(summary = "비밀번호 찾기")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "비밀번호 찾기 성공")])
    fun findPassword(
        @RequestBody @Valid @Schema(implementation = UserServiceFindPasswordRequest::class)
        request: UserFindPasswordRequest
    ): ResponseEntity<DefaultResponse> {
        authService.findPassword(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(PASSWORD_SENT)
    }

    @PostMapping("/api/users/login")
    @Operation(summary = "로그인")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(schema = Schema(implementation = UserLoginResponse::class))]
            )
        ]
    )
    fun login(
        @RequestBody @Valid @Schema(implementation = UserServiceLoginRequest::class)
        request: UserLoginRequest
    ): ResponseEntity<DefaultSingleResponse> {
        val response = authService.login(request.toServiceRequest())

        val headers = jwtProvider.createToken(response.userId)

        return DefaultSingleResponse.toResponseEntity(headers, USER_LOGIN, response)
    }

    @PatchMapping("/api/users/password")
    @Operation(summary = "비밀번호 변경")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "비밀번호 변경 성공")])
    fun changePassword(
        principal: Principal,
        @RequestBody @Valid @Schema(implementation = UserServiceChangePasswordRequest::class)
        request: UserChangePasswordRequest
    ): ResponseEntity<DefaultResponse> {
        authService.changePassword(UserUtility.getUserId(principal), request.toServiceRequest())

        return DefaultResponse.toResponseEntity(PASSWORD_CHANGED)
    }

    @PostMapping("/api/users/token")
    @Operation(summary = "토큰 재발급")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "토큰 재발급 성공")])
    fun refreshToken(principal: Principal): ResponseEntity<DefaultResponse> {
        val headers = jwtProvider.createToken(UserUtility.getUserId(principal))

        return DefaultResponse.toResponseEntity(headers, TOKEN_REFRESHED)
    }
}