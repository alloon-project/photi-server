package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.user.request.*
import com.alloon.alloonserver.common.constant.CustomHttpHeaders.Companion.REFRESH_TOKEN
import com.alloon.alloonserver.config.auth.JwtProvider
import com.alloon.alloonserver.service.user.AuthService
import com.alloon.alloonserver.service.user.dto.ContactServiceSendVerificationDto
import com.alloon.alloonserver.service.user.response.UserLoginResponse
import com.alloon.alloonserver.service.user.response.UserRegisterResponse
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerTest : RestDocsSupport() {

    private val authService = mockk<AuthService>()
    private val jwtProvider = mockk<JwtProvider>()

    override fun initController(): Any {
        return AuthController(authService, jwtProvider)
    }

    @DisplayName("이메일 인증코드 전송을 하면 201을 반환한다")
    @Test
    fun givenValid_whenSendEmail_thenReturn201() {
        // given
        val request = ContactSendVerificationRequest("tester@photi.com")

        every { authService.sendVerificationCode(any(ContactServiceSendVerificationDto::class)) } just Runs

        // when
        val resultActions = mockMvc.perform(
            post("/api/contacts")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isCreated)
            .andExpect(jsonPath("$.successMessage").value("이메일 인증코드를 보냈습니다."))
    }

    @DisplayName("회원 가입을 하면 201을 반환한다")
    @Test
    fun givenValid_whenRegister_thenReturn201() {
        // given
        val request =
            UserRegisterRequest("tester@photi.com", "000000", "tester", "password1!", "password1!")
        val response = UserRegisterResponse(1L, request.username)

        every { authService.registerUser(any()) } returns response
        every { jwtProvider.createToken(any()) } returns HttpHeaders().apply {
            set(AUTHORIZATION, "access-token")
            set(REFRESH_TOKEN, "refresh-token")
        }

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isCreated)
            .andExpect(jsonPath("$.userId").value(response.userId))
            .andExpect(jsonPath("$.username").value(response.username))
    }

    @DisplayName("아이디 찾기를 하면 200을 반환한다")
    @Test
    fun givenValid_whenFindUsername_thenReturn200() {
        // given
        val request = UserFindUsernameRequest("tester@photi.com")

        every { authService.findUsername(any()) } just Runs

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/find-username")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.successMessage").value("아이디를 이메일로 전송했습니다."))
    }

    @DisplayName("비밀번호 찾기를 하면 200을 반환한다")
    @Test
    fun givenValid_whenFindPassword_thenReturn200() {
        // given
        val request = UserFindPasswordRequest("tester@photi.com", "tester")

        every { authService.findPassword(any()) } just Runs

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/find-password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.successMessage").value("임시 비밀번호를 이메일로 전송했습니다."))
    }

    @DisplayName("회원 로그인을 하면 200을 반환한다")
    @Test
    fun givenValid_whenLogin_thenReturn200() {
        // given
        val request = UserLoginRequest("tester", "password1!")
        val response = UserLoginResponse(1, request.username, "", false)

        every { authService.login(request.toServiceDto()) } returns response
        every { jwtProvider.createToken(any()) } returns HttpHeaders().apply {
            set(AUTHORIZATION, "access-token")
            set(REFRESH_TOKEN, "refresh-token")
        }

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(response.userId))
            .andExpect(jsonPath("$.username").value(response.username))
            .andExpect(jsonPath("$.imageUrl").value(response.imageUrl))
            .andExpect(jsonPath("$.temporaryPasswordYn").value(response.temporaryPasswordYn))
    }

    @DisplayName("비밀번호 변경을 하면 200을 반환한다")
    @Test
    fun givenValid_whenChangePassword_thenReturn200() {
        // given
        val request = UserChangePasswordRequest("password1!", "password2!", "password2!")

        every { authService.changePassword(any(), request.toServiceDto()) } just Runs

        // when
        val resultActions = mockMvc.perform(
            patch("/api/users/password")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.successMessage").value("비밀번호가 변경되었습니다."))
    }

    @DisplayName("토큰을 재발급하면 200을 반환한다")
    @Test
    fun givenValid_whenRefreshToken_thenReturn200() {
        // given
        every { jwtProvider.createToken(any()) } returns HttpHeaders().apply {
            set(AUTHORIZATION, "access-token")
            set(REFRESH_TOKEN, "refresh-token")
        }

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/token")
                .header(REFRESH_TOKEN, "refresh-token")
                .principal(mockPrincipal)
        )

        // then
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.successMessage").value("토큰이 재발급 됐습니다."))
    }

    @DisplayName("이메일 인증코드 검증을 하면 200을 반환한다")
    @Test
    fun givenValid_whenVerifyEmailVerificationCode_thenReturn200() {
        // given
        val request = ContactVerifyRequest("tester@photi.com", "000000")

        every { authService.verifyEmailVerificationCode(request.toServiceDto()) } just Runs

        // when
        val resultActions = mockMvc.perform(
            patch("/api/contacts/verify")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.successMessage").value("이메일 인증코드가 확인 되었습니다."))
    }

    @DisplayName("이메일 미입력시 이메일 인증코드 검증을 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenVerifyEmailVerificationCode_thenReturn400() {
        // given
        val request = ContactVerifyRequest("", "000000")

        // when
        val resultActions = mockMvc.perform(
            patch("/api/contacts/verify")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("인증코드 미입력시 이메일 인증코드 검증을 하면 400을 반환한다")
    @Test
    fun givenBlankVerificationCode_whenVerifyEmailVerificationCode_thenReturn400() {
        // given
        val request = ContactVerifyRequest("tester@photi.com", "")

        // when
        val resultActions = mockMvc.perform(
            patch("/api/contacts/verify")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("이메일 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenRegister_thenReturn400() {
        // given
        val request = UserRegisterRequest("", "000000", "tester", "password1!", "password1!")

        every { authService.registerUser(any()) } returns UserRegisterResponse(1, request.username)

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("인증코드 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankVerificationCode_whenRegister_thenReturn400() {
        // given
        val request =
            UserRegisterRequest("tester@photi.com", "", "tester", "password1!", "password1!")

        every { authService.registerUser(any()) } returns UserRegisterResponse(1, request.username)

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("아이디 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankUsername_whenRegister_thenReturn400() {
        // given
        val request =
            UserRegisterRequest("tester@photi.com", "000000", "", "password1!", "password1!")

        every { authService.registerUser(any()) } returns UserRegisterResponse(1, request.username)

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("비밀번호 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankPassword_whenRegister_thenReturn400() {
        // given
        val request = UserRegisterRequest("tester@photi.com", "000000", "tester", "", "password1!")

        every { authService.registerUser(any()) } returns UserRegisterResponse(1, request.username)

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("비밀번호 재입력 미입력시 회원 가입을 하면 400을 반환한다")
    @Test
    fun givenBlankPasswordReEnter_whenRegister_thenReturn400() {
        // given
        val request = UserRegisterRequest("tester@photi.com", "000000", "tester", "password1!", "")

        every { authService.registerUser(any()) } returns UserRegisterResponse(1, request.username)

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/register")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("아이디 미입력시 아이디 찾기를 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenFindUsername_thenReturn400() {
        // given
        val request = UserFindUsernameRequest("")

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/find-username")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("이메일 미입력시 비밀번호 찾기를 하면 400을 반환한다")
    @Test
    fun givenBlankEmail_whenFindPassword_thenReturn400() {
        // given
        val request = UserFindPasswordRequest("", "tester")

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/find-password")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("아이디 미입력시 비밀번호 찾기를 하면 400을 반환한다")
    @Test
    fun givenBlankUsername_whenFindPassword_thenReturn400() {
        // given
        val request = UserFindPasswordRequest("tester@photi.com", "")

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/find-password")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("아이디 미입력시 회원 로그인을 하면 400을 반환한다")
    @Test
    fun givenBlankUsername_whenLogin_thenReturn400() {
        // given
        val request = UserLoginRequest("", "password1!")

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("비밀번호 미입력시 회원 로그인을 하면 400을 반환한다")
    @Test
    fun givenBlankPassword_whenLogin_thenReturn400() {
        // given
        val request = UserLoginRequest("tester", "")

        // when
        val resultActions = mockMvc.perform(
            post("/api/users/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("비밀번호 미입력시 비밀번호 변경을 하면 400을 반환한다")
    @Test
    fun givenBlankPassword_whenChangePassword_thenReturn400() {
        // given
        val request = UserChangePasswordRequest("", "password2!", "password2!")

        // when
        val resultActions = mockMvc.perform(
            patch("/api/users/password")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("새 비밀번호 미입력시 비밀번호 변경을 하면 400을 반환한다")
    @Test
    fun givenBlankNewPassword_whenChangePassword_thenReturn400() {
        // given
        val request = UserChangePasswordRequest("password1!", "", "password2!")

        // when
        val resultActions = mockMvc.perform(
            patch("/api/users/password")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }

    @DisplayName("새 비밀번호 재입력 미입력시 비밀번호 변경을 하면 400을 반환한다")
    @Test
    fun givenBlankNewPasswordReEnter_whenChangePassword_thenReturn400() {
        // given
        val request = UserChangePasswordRequest("password1!", "password2!", "")

        // when
        val resultActions = mockMvc.perform(
            patch("/api/users/password")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isBadRequest)
    }
}