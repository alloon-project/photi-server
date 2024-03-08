package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.user.request.ContactSendVerificationRequest
import com.alloon.alloonserver.api.controller.user.request.ContactVerifyRequest
import com.alloon.alloonserver.api.service.user.AuthService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
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
            MockMvcRequestBuilders.post("/api/v1/contacts")
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
            MockMvcRequestBuilders.post("/api/v1/contacts")
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

    @DisplayName("이메일 인증코드 검증을 하면 200을 반환한다")
    @Test
    fun givenValid_whenVerifyEmailVerificationCode_thenReturn200() {
        // given
        val request = createValidContactVerifyRequest()

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/contacts/verify")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/verify-email-verification-code",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                            .description("인증코드")
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

    @DisplayName("이메일 미입력시 이메일 인증코드 검증을 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenVerifyEmailVerificationCode_thenReturn400() {
        // given
        val request = createValidContactVerifyRequest()
        request.email = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/contacts/verify")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/verify-email-verification-code/email-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                            .description("인증코드")
                    )
                )
            )
    }

    @DisplayName("이메일 미입력시 이메일 인증코드 검증을 하면 400을 반환한다")
    @Test
    fun givenBlankVerificationCode_whenVerifyEmailVerificationCode_thenReturn400() {
        // given
        val request = createValidContactVerifyRequest()
        request.verificationCode = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/contacts/verify")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/verify-email-verification-code/verification-code-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                            .description("인증코드")
                    )
                )
            )
    }

    @DisplayName("아이디 검증을 하면 200을 반환한다")
    @Test
    fun givenValid_whenValidateUsername_thenReturn200() {
        // given
        val username = "tester"

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/users/username")
                .queryParam("username", username)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/validate-username",
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("username")
                            .description("아이디")
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

    @DisplayName("아이디 미입력시 아이디 검증을 하면 400을 반환한다")
    @Test
    fun givenBlankUsername_whenValidateUsername_thenReturn200() {
        // given
        val username = null

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/users/username")
                .queryParam("username", username)
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/validate-username/username-field-required",
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("username")
                            .description("아이디")
                    )
                )
            )
    }

    private fun createValidContactVerifyRequest(): ContactVerifyRequest {
        return ContactVerifyRequest("tester@alloon.com", "000000")
    }

    private fun createValidContactSendVerificationRequest(): ContactSendVerificationRequest {
        return ContactSendVerificationRequest("tester@alloon.com")
    }
}