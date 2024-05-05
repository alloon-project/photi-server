package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.user.request.*
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.api.service.user.request.UserServiceValidateUsernameRequest
import com.alloon.alloonserver.common.constant.SuccessCode.*
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.config.auth.JwtProvider
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Validated
@RestController
class AuthController(
    private val authService: AuthService,
    private val jwtProvider: JwtProvider,
) {

    @PostMapping("/api/contacts")
    fun sendVerificationCode(@RequestBody @Valid request: ContactSendVerificationRequest):
            ResponseEntity<DefaultResponse> {
        authService.sendVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_SENT)
    }

    @PatchMapping("/api/contacts/verify")
    fun verifyEmailVerificationCode(@RequestBody @Valid request: ContactVerifyRequest): ResponseEntity<DefaultResponse> {
        authService.verifyEmailVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_VERIFIED)
    }

    @GetMapping("/api/users/username")
    fun validateUsername(@RequestParam("username") @NotBlank(message = "아이디는 필수 입력입니다.") username: String):
            ResponseEntity<DefaultResponse> {
        authService.validateUsername(UserServiceValidateUsernameRequest(username))

        return DefaultResponse.toResponseEntity(USERNAME_AVAILABLE)
    }

    @PostMapping("/api/users/register")
    fun registerUser(@RequestBody @Valid request: UserRegisterRequest): ResponseEntity<DefaultSingleResponse> {
        val response = authService.registerUser(request.toServiceRequest())

        val headers = jwtProvider.createToken(response.userId)

        return DefaultSingleResponse.toResponseEntity(headers, USER_REGISTERED, response)
    }

    @PostMapping("/api/users/find-username")
    fun findUsername(@RequestBody @Valid request: UserFindUsernameRequest): ResponseEntity<DefaultResponse> {
        authService.findUsername(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(USERNAME_SENT)
    }

    @PostMapping("/api/users/find-password")
    fun findPassword(@RequestBody @Valid request: UserFindPasswordRequest): ResponseEntity<DefaultResponse> {
        authService.findPassword(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(PASSWORD_SENT)
    }

    @PostMapping("/api/users/login")
    fun login(@RequestBody @Valid request: UserLoginRequest): ResponseEntity<DefaultSingleResponse> {
        val response = authService.login(request.toServiceRequest())

        val headers = jwtProvider.createToken(response.userId)

        return DefaultSingleResponse.toResponseEntity(headers, USER_LOGIN, response)
    }

    @PatchMapping("/api/users/password")
    fun changePassword(principal: Principal, @RequestBody @Valid request: UserChangePasswordRequest):
            ResponseEntity<DefaultResponse> {
        authService.changePassword(UserUtility.getUserId(principal), request.toServiceRequest())

        return DefaultResponse.toResponseEntity(PASSWORD_CHANGED)
    }

    @PostMapping("/api/users/token")
    fun refreshToken(principal: Principal): ResponseEntity<DefaultResponse> {
        val headers = jwtProvider.createToken(UserUtility.getUserId(principal))

        return DefaultResponse.toResponseEntity(headers, TOKEN_REFRESHED)
    }
}