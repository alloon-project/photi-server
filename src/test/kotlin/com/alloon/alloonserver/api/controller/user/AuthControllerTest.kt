package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.user.request.*
import com.alloon.alloonserver.api.service.user.AuthService
import com.alloon.alloonserver.api.service.user.response.UserLoginResponse
import com.alloon.alloonserver.api.service.user.response.UserRegisterResponse
import com.alloon.alloonserver.config.auth.JwtProvider
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders
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
    private val jwtProvider = mock(JwtProvider::class.java)

    override fun initController(): Any {
        return AuthController(authService, jwtProvider)
    }

    @DisplayName("이메일 인증코드 전송을 하면 200을 반환한다")
    @Test
    fun givenValid_whenSendEmail_thenReturn200() {
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
    fun givenBlankEmail__whenSendEmail_thenReturn400() {
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

    @DisplayName("회원 가입을 하면 201을 반환한다")
    @Test
    fun givenValid_whenRegister_thenReturn201() {
        // given
        val request = createValidUserRegisterRequest()

        `when`(authService.registerUser(request.toServiceRequest()))
            .thenReturn(UserRegisterResponse(1, request.username))
        `when`(jwtProvider.createToken(anyLong())).thenReturn(HttpHeaders.EMPTY)

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isCreated)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/register",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                            .description("인증코드"),
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("passwordReEntered").type(JsonFieldType.STRING)
                            .description("비밀번호 재입력")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        PayloadDocumentation.fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                            .description("회원 식별자"),
                        PayloadDocumentation.fieldWithPath("data.username").type(JsonFieldType.STRING)
                            .description("회원 아이디"),
                    )
                )
            )
    }

    @DisplayName("이메일 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenRegister_thenReturn400() {
        // given
        val request = createValidUserRegisterRequest()
        request.email = ""

        `when`(authService.registerUser(request.toServiceRequest()))
            .thenReturn(UserRegisterResponse(1, request.username))
        `when`(jwtProvider.createToken(anyLong())).thenReturn(HttpHeaders.EMPTY)

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/register/email-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                            .description("인증코드"),
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("passwordReEntered").type(JsonFieldType.STRING)
                            .description("비밀번호 재입력")
                    ),
                )
            )
    }

    @DisplayName("인증코드 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankVerificationCode_whenRegister_thenReturn400() {
        // given
        val request = createValidUserRegisterRequest()
        request.verificationCode = ""

        `when`(authService.registerUser(request.toServiceRequest()))
            .thenReturn(UserRegisterResponse(1, request.username))
        `when`(jwtProvider.createToken(anyLong())).thenReturn(HttpHeaders.EMPTY)

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/register/verification-code-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                            .description("인증코드"),
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("passwordReEntered").type(JsonFieldType.STRING)
                            .description("비밀번호 재입력")
                    ),
                )
            )
    }

    @DisplayName("아이디 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankUsername_whenRegister_thenReturn400() {
        // given
        val request = createValidUserRegisterRequest()
        request.username = ""

        `when`(authService.registerUser(request.toServiceRequest()))
            .thenReturn(UserRegisterResponse(1, request.username))
        `when`(jwtProvider.createToken(anyLong())).thenReturn(HttpHeaders.EMPTY)

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/register/username-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                            .description("인증코드"),
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("passwordReEntered").type(JsonFieldType.STRING)
                            .description("비밀번호 재입력")
                    ),
                )
            )
    }

    @DisplayName("비밀번호 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankPassword_whenRegister_thenReturn400() {
        // given
        val request = createValidUserRegisterRequest()
        request.password = ""

        `when`(authService.registerUser(request.toServiceRequest()))
            .thenReturn(UserRegisterResponse(1, request.username))
        `when`(jwtProvider.createToken(anyLong())).thenReturn(HttpHeaders.EMPTY)

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/register/password-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                            .description("인증코드"),
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("passwordReEntered").type(JsonFieldType.STRING)
                            .description("비밀번호 재입력")
                    ),
                )
            )
    }

    @DisplayName("비밀번호 재입력 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankPasswordReEntered_whenRegister_thenReturn400() {
        // given
        val request = createValidUserRegisterRequest()
        request.passwordReEntered = ""

        `when`(authService.registerUser(request.toServiceRequest()))
            .thenReturn(UserRegisterResponse(1, request.username))
        `when`(jwtProvider.createToken(anyLong())).thenReturn(HttpHeaders.EMPTY)

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/register/password-re-entered-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                            .description("인증코드"),
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("passwordReEntered").type(JsonFieldType.STRING)
                            .description("비밀번호 재입력")
                    ),
                )
            )
    }

    @DisplayName("아이디 찾기를 하면 200을 반환한다")
    @Test
    fun givenValid_whenFindUsername_thenReturn200() {
        // given
        val request = createValidUserFindUsernameRequest()
        
        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/find-username")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/find-username",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
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

    @DisplayName("아이디 미입력시 아이디 찾기를 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenFindUsername_thenReturn400() {
        // given
        val request = createValidUserFindUsernameRequest()
        request.email = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/find-username")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/find-username/email-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                    )
                )
            )
    }

    @DisplayName("비밀번호 찾기를 하면 200을 반환한다")
    @Test
    fun givenValid_whenFindPassword_thenReturn200() {
        // given
        val request = createValidUserFindPasswordRequest()

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/find-password")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/find-password",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
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

    @DisplayName("이메일 미입력시 비밀번호 찾기를 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenFindPassword_thenReturn400() {
        // given
        val request = createValidUserFindPasswordRequest()
        request.email = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/find-password")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/find-password/email-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                    )
                )
            )
    }

    @DisplayName("아이디 미입력시 비밀번호 찾기를 하면 400을 반환한다")
    @Test
    fun givenBlankUsername_whenFindPassword_thenReturn400() {
        // given
        val request = createValidUserFindPasswordRequest()
        request.username = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/find-password")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/find-password/username-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일"),
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                    )
                )
            )
    }

    @DisplayName("회원 로그인을 하면 200을 반환한다")
    @Test
    fun givenValid_whenLogin_thenReturn200() {
        // given
        val request = createValidUserLoginRequest()

        `when`(authService.login(request.toServiceRequest()))
            .thenReturn(UserLoginResponse(1, request.username, null, false))
        `when`(jwtProvider.createToken(anyLong())).thenReturn(HttpHeaders.EMPTY)

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/login",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        PayloadDocumentation.fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                            .description("회원 식별자"),
                        PayloadDocumentation.fieldWithPath("data.username").type(JsonFieldType.STRING)
                            .description("회원 아이디"),
                        PayloadDocumentation.fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).optional()
                            .description("회원 프로필 이미지"),
                        PayloadDocumentation.fieldWithPath("data.isTemporaryPassword").type(JsonFieldType.BOOLEAN)
                            .description("임시 비밀번호 여부")
                    )
                )
            )
    }

    @DisplayName("아이디 미입력시 회원 로그인을 하면 400을 반환한다")
    @Test
    fun givenBlankUsername_whenLogin_thenReturn400() {
        // given
        val request = createValidUserLoginRequest()
        request.username = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/login/username-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호")
                    )
                )
            )
    }

    @DisplayName("비밀번호 미입력시 회원 로그인을 하면 400을 반환한다")
    @Test
    fun givenBlankPassword_whenLogin_thenReturn400() {
        // given
        val request = createValidUserLoginRequest()
        request.password = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/login/password-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING)
                            .description("아이디"),
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호")
                    )
                )
            )
    }

    @DisplayName("비밀번호 변경을 하면 200을 반환한다")
    @Test
    fun givenValid_whenChangePassword_thenReturn200() {
        // given
        val request = createValidUserChangePasswordRequest()

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/users/password")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/change-password",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("newPassword").type(JsonFieldType.STRING)
                            .description("새 비밀번호"),
                        PayloadDocumentation.fieldWithPath("newPasswordReEntered").type(JsonFieldType.STRING)
                            .description("새 비밀번호 재입력")
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

    @DisplayName("비밀번호 미입력시 비밀번호 변경을 하면 400을 반환한다")
    @Test
    fun givenBlankPassword_whenChangePassword_thenReturn400() {
        // given
        val request = createValidUserChangePasswordRequest()
        request.password = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/users/password")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/change-password/password-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("newPassword").type(JsonFieldType.STRING)
                            .description("새 비밀번호"),
                        PayloadDocumentation.fieldWithPath("newPasswordReEntered").type(JsonFieldType.STRING)
                            .description("새 비밀번호 재입력")
                    )
                )
            )
    }

    @DisplayName("새 비밀번호 미입력시 비밀번호 변경을 하면 400을 반환한다")
    @Test
    fun givenBlankNewPassword_whenChangePassword_thenReturn400() {
        // given
        val request = createValidUserChangePasswordRequest()
        request.newPassword = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/users/password")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/change-password/new-password-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("newPassword").type(JsonFieldType.STRING)
                            .description("새 비밀번호"),
                        PayloadDocumentation.fieldWithPath("newPasswordReEntered").type(JsonFieldType.STRING)
                            .description("새 비밀번호 재입력")
                    )
                )
            )
    }

    @DisplayName("새 비밀번호 재입력 미입력시 비밀번호 변경을 하면 400을 반환한다")
    @Test
    fun givenBlankNewPasswordReEntered_whenChangePassword_thenReturn400() {
        // given
        val request = createValidUserChangePasswordRequest()
        request.newPasswordReEntered = ""

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/users/password")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/change-password/new-password-re-entered-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        PayloadDocumentation.fieldWithPath("newPassword").type(JsonFieldType.STRING)
                            .description("새 비밀번호"),
                        PayloadDocumentation.fieldWithPath("newPasswordReEntered").type(JsonFieldType.STRING)
                            .description("새 비밀번호 재입력")
                    )
                )
            )
    }

    @DisplayName("토큰을 재발급하면 200을 반환한다")
    @Test
    fun givenValid_whenRefreshToken_thenReturn200() {
        // given
        `when`(jwtProvider.createToken(anyLong())).thenReturn(HttpHeaders.EMPTY)

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/token")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "auth/refresh-token",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    )
                )
            )
    }

    private fun createValidUserChangePasswordRequest(): UserChangePasswordRequest {
        return UserChangePasswordRequest("password1!", "password2!", "password2!")
    }

    private fun createValidUserLoginRequest(): UserLoginRequest {
        return UserLoginRequest("tester", "password1!")
    }

    private fun createValidUserFindPasswordRequest(): UserFindPasswordRequest {
        return UserFindPasswordRequest("tester@alloon.com", "tester")
    }
    
    private fun createValidUserFindUsernameRequest(): UserFindUsernameRequest {
        return UserFindUsernameRequest("tester@alloon.com") 
    }

    private fun createValidUserRegisterRequest(): UserRegisterRequest {
        return UserRegisterRequest("tester@alloon.com", "000000", "tester",
            "password1!", "password1!")
    }

    private fun createValidContactVerifyRequest(): ContactVerifyRequest {
        return ContactVerifyRequest("tester@alloon.com", "000000")
    }

    private fun createValidContactSendVerificationRequest(): ContactSendVerificationRequest {
        return ContactSendVerificationRequest("tester@alloon.com")
    }
}