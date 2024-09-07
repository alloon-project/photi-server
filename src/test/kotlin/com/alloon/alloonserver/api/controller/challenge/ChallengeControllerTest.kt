package com.alloon.alloonserver.api.controller.challenge

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.challenge.request.CreateChallengeHashtagRequest
import com.alloon.alloonserver.api.controller.challenge.request.CreateChallengeRequest
import com.alloon.alloonserver.api.controller.challenge.request.CreateChallengeRuleRequest
import com.alloon.alloonserver.api.controller.challenge.request.UpdateChallengeMemberGoalRequest
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengesResponse
import com.alloon.alloonserver.service.challenge.ChallengeService
import com.alloon.alloonserver.service.challenge.dto.*
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.*
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ChallengeControllerTest : RestDocsSupport() {

    private val challengeService = mockk<ChallengeService>()

    override fun initController(): Any {
        return ChallengeController(challengeService)
    }

    @DisplayName("챌린지 생성을 하면 201을 반환한다")
    @Test
    fun givenValid_whenCreateChallenge_thenReturn201() {
        // given
        val request = getCreateChallengeRequest()
        val challenge = request.toServiceDto()

        every { challengeService.createChallenge(any(), any(), any()) } returns challenge

        // when
        val requestMultipartFile = MockMultipartFile(
            "request",
            "request.json",
            "application/json",
            objectMapper.writeValueAsBytes(request)
        )
        val imageMultipartFile =
            MockMultipartFile("imageFile", "file.png", "image/png", ByteArray(1))
        val resultActions = mockMvc.perform(
            multipart("/api/challenges")
                .file(requestMultipartFile)
                .file(imageMultipartFile)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MULTIPART_FORM_DATA_VALUE)
        )

        // then
        resultActions.andExpect(status().isCreated)
    }

    @DisplayName("챌린지 예시 이미지 전체 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetChallengeExampleImages_thenReturn200() {
        // given
        every { challengeService.getChallengeExampleImages() } returns listOf("https://url.kr/5MhHhD")

        // when
        val resultActions = mockMvc.perform(get("/api/challenges/example-images"))

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지가 있는 경우 지금 인기있는 챌린지 조회를 하면 200을 반환한다")
    @Test
    fun givenChallenge_whenFindPopularChallenges_thenReturn200() {
        // given
        val dto = getFindChallengesDto()

        every { challengeService.findPopularChallenges() } returns listOf(dto)

        // when
        val resultActions = mockMvc.perform(
            get("/api/challenges/popular")
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지가 없는 경우 지금 인기있는 챌린지 조회를 하면 200을 반환한다")
    @Test
    fun givenNoChallenge_whenFindPopularChallenges_thenReturn200() {
        // given
        every { challengeService.findPopularChallenges() } returns listOf()

        // when
        val resultActions = mockMvc.perform(
            get("/api/challenges/popular")
                .param("size", "5")
                .param("sort", "visitCnt,DESC")
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 멤버가 챌린지 소개 조회를 성공하면 200을 반환한다")
    @Test
    fun givenValid_whenFindChallengeInfo_thenReturn200() {
        // given
        val dto = getFindChallengeInfoDto()
        every { challengeService.findChallengeInfo(any()) } returns dto

        // when
        val resultActions = mockMvc.perform(
            get("/api/challenges/{challengeId}/info", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 멤버가 개인목표 작성을 성공하면 200을 반환한다")
    @Test
    fun givenValid_whenUpdateChallengeMemberGoal_thenReturn200() {
        // given
        val request = UpdateChallengeMemberGoalRequest("개인목표")

        every { challengeService.updateChallengeMemberGoal(any(), any(), any()) } just runs

        // when
        val resultActions = mockMvc.perform(
            patch("/api/challenges/{challengeId}/challenge-members/goal", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.successMessage").value("챌린지 개인목표 작성이 완료되었습니다."))
    }

    @DisplayName("챌린지 멤버가 챌린지 파티원 조회를 성공하면 200을 반환한다")
    @Test
    fun givenValid_whenFindChallengeMembers_thenReturn200() {
        // given
        val dto = FindChallengeMembersDto(1L, "tester", "", true, LocalDateTime.now(), "개인목표")

        every { challengeService.findChallengeMembers(any(), any()) } returns listOf(dto)

        // when
        val resultActions = mockMvc.perform(
            get("/api/challenges/{challengeId}/challenge-members", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("모든 챌린지 조회를 성공하면 200을 반환한다")
    @Test
    fun givenValid_whenFindAllChallenges_thenReturn200() {
        // given
        val dto = getFindChallengesDto()
        val content = listOf(FindChallengesResponse.of(dto))
        val pageable = PageRequest.of(0, 10)
        val hasNext = true

        every { challengeService.findAllChallenges(any()) } returns SliceImpl(
            content,
            pageable,
            hasNext
        )

        // when
        val resultActions = mockMvc.perform(get("/api/challenges"))

        // then
        resultActions.andExpect(status().isOk)
    }

    private fun getCreateChallengeRequest(): CreateChallengeRequest {
        return CreateChallengeRequest(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            listOf(
                CreateChallengeRuleRequest("챌린지 인증 룰1"),
                CreateChallengeRuleRequest("챌린지 인증 룰2"),
                CreateChallengeRuleRequest("챌린지 인증 룰3"),
            ),
            listOf(
                CreateChallengeHashtagRequest("해시태그 1"),
                CreateChallengeHashtagRequest("해시태그 2"),
            )
        )
    }

    private fun getFindChallengeInfoDto(): FindChallengeInfoDto {
        return FindChallengeInfoDto(
            listOf(
                ChallengeRuleDto("챌린지 인증 룰1"),
                ChallengeRuleDto("챌린지 인증 룰2"),
                ChallengeRuleDto("챌린지 인증 룰3"),
            ),
            LocalTime.of(13, 0),
            "챌린지 목표입니다.",
            LocalDate.now(),
            LocalDate.of(2024, 12, 1),
        )
    }

    private fun getFindChallengesDto(): FindChallengesDto {
        return FindChallengesDto(
            1L,
            "챌린지 이름",
            LocalDate.of(2024, 12, 1),
            "https://url.kr/5MhHhD",
            listOf("해시태그 1", "해시태그 2")
        )
    }
}