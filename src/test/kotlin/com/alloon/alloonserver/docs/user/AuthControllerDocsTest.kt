package com.alloon.alloonserver.docs.user

import com.alloon.alloonserver.api.controller.user.AuthController
import com.alloon.alloonserver.api.controller.user.request.ContactSendVerificationRequest
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.docs.RestDocsSupport
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerDocsTest : RestDocsSupport() {

    private val authService = mock(AuthService::class.java)

    @Override
    override fun initController(): Any {
        return AuthController(authService)
    }

    @DisplayName("이메일 인증코드 전송을 하면 200을 반환한다")
    @Test
    fun givenValid_whenSendVerificationCode_thenReturn200() {
        // given
        val request = createValidContactSendVerificationRequest()

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/contact")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isCreated)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/send-verification-code",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    )
                )
            )
    }

    @DisplayName("이메일 미입력시 이메일 인증코드 전송을 하면 400을 반환한다")
    @Test
    fun givenBlankEmail__whenSendVerificationCode_thenReturn400() {
        // given
        val request = createValidContactSendVerificationRequest()
        request.email = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/contact")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/send-verification-code/email-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일")
                    )
                )
            )
    }


    private fun createValidContactSendVerificationRequest(): ContactSendVerificationRequest {
        return ContactSendVerificationRequest("tester@alloon.com")
    }
}