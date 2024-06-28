package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.user.request.*
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.api.service.user.request.*
import com.alloon.alloonserver.common.constant.SuccessCode.*
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.config.auth.JwtProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "이메일 인증코드 전송 성공"),
            ApiResponse(
                responseCode = "400",
                description = """
                1. 이메일은 필수 입력입니다.
                2. 이메일은 1~100자만 가능합니다.
                3. 올바른 이메일 형식을 입력해 주세요.
                """
            ),
            ApiResponse(responseCode = "409", description = "이미 사용중인 이메일입니다."),
        ]
    )
    fun sendVerificationCode(
        @RequestBody @Valid @Schema(implementation = ContactServiceSendVerificationRequest::class)
        request: ContactSendVerificationRequest
    ): ResponseEntity<DefaultResponse> {
        authService.sendVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_SENT)
    }

    @PatchMapping("/api/contacts/verify")
    @Operation(summary = "이메일 인증코드 확인")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이메일 인증코드 확인 성공"),
            ApiResponse(
                responseCode = "400",
                description = """
                1. 이메일은 필수 입력입니다.
                2. 이메일은 1~100자만 가능합니다.
                3. 올바른 이메일 형식을 입력해 주세요.
                4. 이메일 인증코드는 필수 입력입니다.
                5. 이메일 인증코드가 틀렸습니다.
                """
            ),
            ApiResponse(responseCode = "404", description = "존재하지 않는 이메일입니다."),
        ]
    )
    fun verifyEmailVerificationCode(
        @RequestBody @Valid @Schema(implementation = ContactServiceVerifyRequest::class)
        request: ContactVerifyRequest
    ): ResponseEntity<DefaultResponse> {
        authService.verifyEmailVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_VERIFIED)
    }

    @GetMapping("/api/users/username")
    @Operation(summary = "아이디 검증")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "아이디 검증 성공"),
            ApiResponse(
                responseCode = "400",
                description = """
                1. 아이디는 필수 입력입니다.
                2. 아이디는 5~20자만 가능합니다.
                3. 아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요.
                """
            ),
            ApiResponse(
                responseCode = "409",
                description = """
                1. 사용 불가능한 아이디입니다.
                2. 이미 사용중인 아이디입니다.
                """
            ),
        ]
    )
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
            ApiResponse(responseCode = "201", description = "회원가입 성공"),
            ApiResponse(
                responseCode = "400",
                description = """
                1. 이메일은 필수 입력입니다.
                2. 이메일은 1~100자만 가능합니다.
                3. 올바른 이메일 형식을 입력해 주세요.
                4. 이메일 인증코드는 필수 입력입니다.
                5. 아이디는 필수 입력입니다.
                6. 아이디는 5~20자만 가능합니다.
                7. 아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요.
                8. 비밀번호는 필수 입력입니다.
                9. 비밀번호는 8~30자만 가능합니다.
                10. 비밀번호는 영어, 숫자, 특수문자(#$@!%&*)의 조합으로 입력해 주세요.
                11. 비밀번호 재입력은 필수 입력입니다.
                12. 이메일 인증을 먼저 해주세요.
                13. 비밀번호와 비밀번호 재입력이 동일하지 않습니다.
                """
            ),
            ApiResponse(
                responseCode = "409",
                description = """
                1. 해당 이메일로 이미 가입된 회원이 있습니다.
                2. 사용 불가능한 아이디입니다.
                3. 이미 사용중인 아이디입니다.
                """
            ),
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
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "아이디 찾기 성공"),
            ApiResponse(responseCode = "400", description = "이메일은 필수 입력입니다."),
            ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다."),
        ]
    )
    fun findUsername(
        @RequestBody @Valid @Schema(implementation = UserServiceFindUsernameRequest::class)
        request: UserFindUsernameRequest
    ): ResponseEntity<DefaultResponse> {
        authService.findUsername(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(USERNAME_SENT)
    }

    @PostMapping("/api/users/find-password")
    @Operation(summary = "비밀번호 찾기")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "비밀번호 찾기 성공"),
            ApiResponse(
                responseCode = "400",
                description = """
                1. 이메일은 필수 입력입니다.
                2. 아이디는 필수 입력입니다.
                """
            ),
            ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다."),
        ]
    )
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
            ApiResponse(responseCode = "200", description = "로그인 성공"),
            ApiResponse(
                responseCode = "400",
                description = """
                1. 아이디는 필수 입력입니다.
                2. 비밀번호는 필수 입력입니다.
                """
            ),
            ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 틀렸습니다."),
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
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            ApiResponse(
                responseCode = "400",
                description = """
                1. 비밀번호는 필수 입력입니다.
                2. 새 비밀번호는 필수 입력입니다.
                3. 비밀번호는 8~30자만 가능합니다.
                4. 비밀번호는 영어, 숫자, 특수문자(#$@!%&*)의 조합으로 입력해 주세요.
                5. 새 비밀번호 재입력은 필수 입력입니다.
                6. 비밀번호와 비밀번호 재입력이 동일하지 않습니다.
                """
            ),
            ApiResponse(
                responseCode = "401",
                description = """
                1. 승인되지 않은 요청입니다. 다시 로그인 해주세요.
                2. 아이디 또는 비밀번호가 틀렸습니다.
                """
            ),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
        ]
    )
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
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
        ]
    )
    fun refreshToken(principal: Principal): ResponseEntity<DefaultResponse> {
        val headers = jwtProvider.createToken(UserUtility.getUserId(principal))

        return DefaultResponse.toResponseEntity(headers, TOKEN_REFRESHED)
    }
}