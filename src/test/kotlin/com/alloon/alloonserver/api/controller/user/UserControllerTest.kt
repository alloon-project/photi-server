package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.service.user.UserService
import com.alloon.alloonserver.service.user.dto.UserChallengeHistoryDto
import com.alloon.alloonserver.service.user.dto.UserInfoDto
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.MULTIPART_FORM_DATA
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest : RestDocsSupport() {

    private val userService = mockk<UserService>()

    override fun initController(): Any {
        return UserController(userService)
    }

    @DisplayName("사용자 정보 조회를 성공하면 200을 반환한다")
    @Test
    fun givenValid_whenFindUserInfo_thenReturn200() {
        // given
        val dto = getUserInfoDto()

        every { userService.findUserInfo(any()) } returns dto

        // when
        val resultActions = mockMvc.perform(
            get("/api/users")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("사용자 프로필 이미지 업로드를 성공하면 200을 반환한다.")
    @Test
    fun givenValid_whenUpdateUserImage_thenReturn200() {
        // given
        val dto = getUserInfoDto()
        val multipartFile = MockMultipartFile("imageFile", "file.png", "image/png", ByteArray(1))

        every { userService.updateUserImage(any(), any()) } returns dto

        // when
        val resultActions = mockMvc.perform(
            multipart("/api/users/image")
                .file(multipartFile)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MULTIPART_FORM_DATA)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("사용자 챌린지 기록 조회를 성공하면 200을 반환한다.")
    @Test
    fun givenValid_whenFindUserChallengeHistory_thenReturn200() {
        // given
        val dto = getUserChallengeHistoryDto()

        every { userService.findUserChallengeHistory(any()) } returns dto

        // when
        val resultActions = mockMvc.perform(
            get("/api/users/challenge-history")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("사용자 피드 인증 날짜 리스트 조회를 성공하면 200을 반환한다.")
    @Test
    fun givenValid_whenFindUserFeeds_thenReturn200() {
        // given
        val response = listOf("2024-10-02", "2024-10-05", "2024-10-07")

        every { userService.findUserFeeds(any()) } returns response

        // when
        val resultActions = mockMvc.perform(
            get("/api/users/feeds")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    private fun getUserInfoDto(): UserInfoDto {
        return UserInfoDto("https://url.kr/5MhHhD", "tester", "tester@photi.com")
    }

    private fun getUserChallengeHistoryDto(): UserChallengeHistoryDto {
        return UserChallengeHistoryDto("tester", "https://url.kr/5MhHhD", 99, 2)
    }
}