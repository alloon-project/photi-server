package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.user.request.ContactSendVerificationRequest
import com.alloon.alloonserver.api.controller.user.request.ContactVerifyRequest
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.common.constant.SuccessCode.*
import com.alloon.alloonserver.common.response.DefaultResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AuthController(
    private val authService: AuthService,
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
    fun validateUsername(@RequestParam(value = "username") username: String): ResponseEntity<DefaultResponse> {
        authService.validateUsername(username)

        return DefaultResponse.toResponseEntity(USERNAME_AVAILABLE)
    }
}