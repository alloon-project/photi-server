package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.user.request.ContactSendVerificationRequest
import com.alloon.alloonserver.api.controller.user.request.ContactVerifyRequest
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.common.constant.SuccessCode.EMAIL_VERIFICATION_CODE_SENT
import com.alloon.alloonserver.common.constant.SuccessCode.EMAIL_VERIFICATION_CODE_VERIFIED
import com.alloon.alloonserver.common.response.DefaultResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/api/v1/contact")
    fun sendVerificationCode(@RequestBody @Valid request: ContactSendVerificationRequest):
            ResponseEntity<DefaultResponse> {
        authService.sendVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_SENT)
    }

    @PatchMapping("/api/v1/contact")
    fun verifyEmailVerificationCode(@RequestBody @Valid request: ContactVerifyRequest): ResponseEntity<DefaultResponse> {
        authService.verifyEmailVerificationCode(request.toServiceRequest())

        return DefaultResponse.toResponseEntity(EMAIL_VERIFICATION_CODE_VERIFIED)
    }
}