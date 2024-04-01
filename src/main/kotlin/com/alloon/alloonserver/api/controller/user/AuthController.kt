package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.user.request.ContactSendVerificationRequest
import com.alloon.alloonserver.api.controller.user.request.ContactVerifyRequest
import com.alloon.alloonserver.api.controller.user.request.UserRegisterRequest
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.common.constant.SuccessCode.*
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.config.auth.JwtProvider
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
class AuthController(
    private val authService: AuthService,
    private val jwtProvider: JwtProvider,
) {

    @PostMapping("/api/v1/contacts")
    fun sendVerificationCode(@RequestBody @Valid request: ContactSendVerificationRequest):
            ResponseEntity<DefaultResponse> {
        authService.sendVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_SENT)
    }

    @PatchMapping("/api/v1/contacts/verify")
    fun verifyEmailVerificationCode(@RequestBody @Valid request: ContactVerifyRequest): ResponseEntity<DefaultResponse> {
        authService.verifyEmailVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_VERIFIED)
    }

    @GetMapping("/api/v1/users/username")
    fun validateUsername(@RequestParam("username") @NotBlank(message = "아이디는 필수 입력입니다.") username: String):
            ResponseEntity<DefaultResponse> {
        authService.validateUsername(username)

        return DefaultResponse.toResponseEntity(USERNAME_AVAILABLE)
    }

    @PostMapping("/api/v1/users/register")
    fun registerUser(@RequestBody @Valid request: UserRegisterRequest): ResponseEntity<DefaultSingleResponse> {
        val response = authService.registerUser(request.toServiceRequest())

        val headers = jwtProvider.createToken(response.userId)

        return DefaultSingleResponse.toResponseEntity(headers, USER_REGISTERED, response)
    }
}