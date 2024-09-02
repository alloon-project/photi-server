package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.user.request.*
import com.alloon.alloonserver.common.constant.RegexPatternConstants
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
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "이메일 인증코드 전송 성공"),
            ApiResponse(responseCode = "409", description = "이미 사용중인 이메일입니다."),
        ]
    )
    fun sendVerificationCode(@RequestBody @Valid request: ContactSendVerificationRequest): ResponseEntity<StringResponse> {
        authService.sendVerificationCode(request.toServiceDto())

        return ResponseEntity.status(CREATED)
            .body(StringResponse("이메일 인증코드를 보냈습니다."))
    }

    @PatchMapping("/api/contacts/verify")
    @Operation(summary = "이메일 인증코드 확인")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이메일 인증코드 확인 성공"),
            ApiResponse(responseCode = "400", description = "이메일 인증코드가 틀렸습니다."),
            ApiResponse(responseCode = "404", description = "존재하지 않는 이메일입니다."),
        ]
    )
    fun verifyEmailVerificationCode(@RequestBody @Valid request: ContactVerifyRequest): ResponseEntity<StringResponse> {
        authService.verifyEmailVerificationCode(request.toServiceDto())

        return ResponseEntity.ok(StringResponse("이메일 인증코드가 확인 되었습니다."))
    }

    @GetMapping("/api/users/username")
    @Operation(summary = "아이디 검증")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "아이디 검증 성공"),
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
        @Size(min = 5, max = 20, message = "아이디는 5~20자만 가능합니다.")
        @Pattern(
            regexp = RegexPatternConstants.LOWERCASE_NUMBER_UNDERSCORE,
            message = "아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요."
        )
        @Parameter(description = "아이디", example = "photi_123")
        username: String
    ): ResponseEntity<StringResponse> {
        authService.validateUsername(UserServiceValidateUsernameDto(username))

        return ResponseEntity.ok(StringResponse("사용 가능한 아이디입니다."))
    }

    @PostMapping("/api/users/register")
    @Operation(summary = "회원가입")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "회원가입 성공"),
            ApiResponse(
                responseCode = "400",
                description = """
                1. 이메일 인증을 먼저 해주세요.
                2. 비밀번호와 비밀번호 재입력이 동일하지 않습니다.
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
    fun registerUser(@RequestBody @Valid request: UserRegisterRequest): ResponseEntity<UserRegisterResponse> {
        val response = authService.registerUser(request.toServiceDto())
        val headers = jwtProvider.createToken(response.userId)

        return ResponseEntity.status(CREATED).headers(headers).body(response)
    }

    @PostMapping("/api/users/find-username")
    @Operation(summary = "아이디 찾기")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "아이디 찾기 성공"),
            ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다."),
        ]
    )
    fun findUsername(@RequestBody @Valid request: UserFindUsernameRequest): ResponseEntity<StringResponse> {
        authService.findUsername(request.toServiceDto())

        return ResponseEntity.ok(StringResponse("아이디를 이메일로 전송했습니다."))
    }

    @PostMapping("/api/users/find-password")
    @Operation(summary = "비밀번호 찾기")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "비밀번호 찾기 성공"),
            ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다."),
        ]
    )
    fun findPassword(@RequestBody @Valid request: UserFindPasswordRequest): ResponseEntity<StringResponse> {
        authService.findPassword(request.toServiceDto())

        return ResponseEntity.ok(StringResponse("임시 비밀번호를 이메일로 전송했습니다."))
    }

    @PostMapping("/api/users/login")
    @Operation(summary = "로그인")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "로그인 성공"),
            ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 틀렸습니다."),
        ]
    )
    fun login(@RequestBody @Valid request: UserLoginRequest): ResponseEntity<UserLoginResponse> {
        val response = authService.login(request.toServiceDto())
        val headers = jwtProvider.createToken(response.userId)

        return ResponseEntity.status(OK).headers(headers).body(response)
    }

    @PatchMapping("/api/users/password")
    @Operation(summary = "비밀번호 변경", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            ApiResponse(responseCode = "400", description = "비밀번호와 비밀번호 재입력이 동일하지 않습니다."),
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
        @RequestBody @Valid request: UserChangePasswordRequest
    ): ResponseEntity<StringResponse> {
        authService.changePassword(UserUtility.getUserId(principal), request.toServiceDto())

        return ResponseEntity.ok(StringResponse("비밀번호가 변경되었습니다."))
    }

    @PostMapping("/api/users/token")
    @Operation(summary = "토큰 재발급", security = [SecurityRequirement(name = REFRESH_TOKEN_KEY)])
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
        ]
    )
    fun refreshToken(principal: Principal): ResponseEntity<StringResponse> {
        val headers = jwtProvider.createToken(UserUtility.getUserId(principal))

        return ResponseEntity.status(OK)
            .headers(headers)
            .body(StringResponse("토큰이 재발급 됐습니다."))
    }
}