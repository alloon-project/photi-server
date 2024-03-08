package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.WebMvcSupport
import com.alloon.alloonserver.api.controller.user.request.ContactSendVerificationRequest
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.common.constant.ExceptionCode.EMAIL_FIELD_REQUIRED
import com.alloon.alloonserver.common.constant.SuccessCode.VERIFICATION_CODE_SENT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
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
            post("/api/v1/contact")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code")
                .value(VERIFICATION_CODE_SENT.name))
            .andExpect(jsonPath("$.message")
                .value(VERIFICATION_CODE_SENT.message))
    }

    @DisplayName("이메일 미입력시 이메일 인증코드 전송을 하면 400을 반환한다")
    fun givenBlankEmail_whenSendVerificationCode_thenReturn400() {
        // given
        val request = createValidContactSendVerificationRequest()
        request.email = ""

        // when & then
        mockMvc.perform(
            post("/api/v1/contact")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code")
                .value(EMAIL_FIELD_REQUIRED.name))
            .andExpect(jsonPath("$.message")
                .value(EMAIL_FIELD_REQUIRED.message))
    }

    private fun createValidContactSendVerificationRequest(): ContactSendVerificationRequest {
        return ContactSendVerificationRequest("tester@alloon.com")
    }
}