package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.WebMvcSupport
import com.alloon.alloonserver.api.controller.user.request.ContactSendVerificationRequest
import com.alloon.alloonserver.api.controller.user.request.ContactVerifyRequest
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.common.constant.ExceptionCode.EMAIL_FIELD_REQUIRED
import com.alloon.alloonserver.common.constant.ExceptionCode.VERIFICATION_CODE_FIELD_REQUIRED
import com.alloon.alloonserver.common.constant.SuccessCode.EMAIL_VERIFICATION_CODE_VERIFIED
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController::class)
abstract class AuthControllerTest() : WebMvcSupport() {

    @MockBean private lateinit var authService: AuthService

    @DisplayName("이메일 인증코드 전송을 하면 201을 반환한다")
    @Test
    fun givenValid_whenSendVerificationCode_thenReturn201() {
        // given
        val request = createValidContactSendVerificationRequest()

        // when & then
        mockMvc.perform(
            post("/api/v1/contacts")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code")
                .value(EMAIL_VERIFICATION_CODE_VERIFIED.name))
            .andExpect(jsonPath("$.message")
                .value(EMAIL_VERIFICATION_CODE_VERIFIED.message))
    }

    @DisplayName("이메일 미입력시 이메일 인증코드 전송을 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenSendVerificationCode_thenReturn400() {
        // given
        val request = createValidContactSendVerificationRequest()
        request.email = ""

        // when & then
        mockMvc.perform(
            post("/api/v1/contacts")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code")
                .value(EMAIL_FIELD_REQUIRED.name))
            .andExpect(jsonPath("$.message")
                .value(EMAIL_FIELD_REQUIRED.message))
    }

    @DisplayName("이메일 인증코드 검증을 하면 200을 반환한다")
    @Test
    fun givenValid_whenVerifyEmailVerificationCode_thenReturn200() {
        // given
        val request = createValidContactVerifyRequest()

        // when & then
        mockMvc.perform(
            patch("/api/v1/contacts/verify")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code")
                .value(EMAIL_VERIFICATION_CODE_VERIFIED.name))
            .andExpect(jsonPath("$.message")
                .value(EMAIL_VERIFICATION_CODE_VERIFIED.message))
    }

    @DisplayName("이메일 미입력시 이메일 인증코드 검증을 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenVerifyEmailVerificationCode_thenReturn400() {
        // given
        val request = createValidContactVerifyRequest()
        request.email = ""

        // when & then
        mockMvc.perform(
            patch("/api/v1/contacts/verify")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code")
                .value(EMAIL_FIELD_REQUIRED.name))
            .andExpect(jsonPath("$.message")
                .value(EMAIL_FIELD_REQUIRED.message))
    }

    @DisplayName("인증코드 미입력시 이메일 인증코드 검증을 하면 400을 반환한다")
    @Test
    fun givenBlankVerificationCode_whenVerifyEmailVerificationCode_thenReturn400() {
        // given
        val request = createValidContactVerifyRequest()
        request.verificationCode = ""

        // when & then
        mockMvc.perform(
            patch("/api/v1/contacts/verify")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code")
                .value(VERIFICATION_CODE_FIELD_REQUIRED.name))
            .andExpect(jsonPath("$.message")
                .value(VERIFICATION_CODE_FIELD_REQUIRED.message))
    }

    private fun createValidContactVerifyRequest(): ContactVerifyRequest {
        return ContactVerifyRequest("tester@alloon.com", "000000")
    }

    private fun createValidContactSendVerificationRequest(): ContactSendVerificationRequest {
        return ContactSendVerificationRequest("tester@alloon.com")
    }
}