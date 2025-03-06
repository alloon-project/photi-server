package com.alloon.alloonserver.api.controller.challenge

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.challenge.request.*
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengeFeedCommentsResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengeFeedsByDateResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengesResponse
import com.alloon.alloonserver.service.challenge.ChallengeService
import com.alloon.alloonserver.service.challenge.dto.*
import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpMethod.PATCH
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
        val dto = getFindPopularChallengesDto()

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

    @DisplayName("챌린지 개별 조회를 성공하면 200을 반환한다")
    @Test
    fun givenValid_whenFindAllChallenge_thenReturn200() {
        // given
        every { challengeService.findChallenge(any()) } returns getFindChallengeDto()

        // when
        val resultActions = mockMvc.perform(
            get("/api/challenges/{challengeId}", 1)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 수정을 성공하면 200을 반환한다")
    @Test
    fun givenValid_whenUpdateChallenge_thenReturn200() {
        // given
        val request = getUpdateChallengeRequest()

        every { challengeService.updateChallenge(any(), any(), any(), any()) } just Runs

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
            multipart(PATCH, "/api/challenges/{challengeId}", 1)
                .file(requestMultipartFile)
                .file(imageMultipartFile)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MULTIPART_FORM_DATA_VALUE)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 탈퇴를 성공하면 200을 반환한다.")
    @Test
    fun givenValid_whenDeleteChallenge_thenReturn200() {
        // given
        every { challengeService.deleteChallenge(any(), any()) } just Runs

        // when
        val resultActions = mockMvc.perform(
            delete("/api/challenges/{challengeId}", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 피드 삭제를 성공하면 200을 반환한다.")
    @Test
    fun givenValid_whenDeleteChallengeFeed_thenReturn200() {
        // given
        every { challengeService.deleteChallengeFeed(any(), any(), any()) } just Runs

        // when
        val resultActions = mockMvc.perform(
            delete("/api/challenges/{challengeId}/feeds/{feedId}", 1, 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 피드 조회를 성공하면 200을 반환한다.")
    @Test
    fun givenValid_whenFindChallengeFeeds_thenReturn200() {
        // given
        val feeds = listOf(getFindChallengeFeedsDto())
        val dto = Triple(LocalDate.now(), feeds, 100)
        val content = listOf(FindChallengeFeedsByDateResponse.of(dto))
        val pageable = PageRequest.of(0, 10)
        val hasNext = true

        every { challengeService.findChallengeFeeds(any(), any(), any(), any()) } returns SliceImpl(
            content,
            pageable,
            hasNext
        )

        // when
        val resultActions = mockMvc.perform(
            get("/api/challenges/{challengeId}/feeds", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 피드 댓글 삭제를 성공하면 200을 반환한다.")
    @Test
    fun givenValid_whenDeleteChallengeFeedComment_thenReturn200() {
        // given
        every { challengeService.deleteChallengeFeedComment(any(), any(), any(), any()) } just Runs

        // when
        val resultActions = mockMvc.perform(
            delete("/api/challenges/{challengeId}/feeds/{feedId}/comments/{commentId}", 1, 1, 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 피드 개별 조회를 성공하면 200을 반환한다.")
    @Test
    fun givenValid_whenFindChallengeFeed_thenReturn200() {
        // given
        val dto = getFindChallengeFeedDto()
        every { challengeService.findChallengeFeed(any(), any(), any()) } returns dto

        // when
        val resultActions = mockMvc.perform(
            get("/api/challenges/{challengeId}/feeds/{feedId}", 1, 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 피드 댓글 리스트 조회를 성공하면 200을 반환한다")
    @Test
    fun givenValid_whenFindChallengeFeedComments_thenReturn200() {
        // given
        val dto = FindChallengeFeedCommentsDto(1L, "tester", "피드 댓글")
        val content = listOf(FindChallengeFeedCommentsResponse.of(dto))
        val pageable = PageRequest.of(0, 10)
        val hasNext = true

        every { challengeService.findChallengeFeedComments(any(), any()) } returns SliceImpl(
            content,
            pageable,
            hasNext
        )

        // when
        val resultActions = mockMvc.perform(
            get("/api/challenges/feeds/{feedId}/comments", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지 초대코드 조회를 성공하면 200을 반환한다.")
    @Test
    fun givenValid_whenFindChallengeInvitationCode_thenReturn200() {
        // given
        val dto = FindChallengeInvitationCodeDto("챌린지 이름", "ABC12")
        every { challengeService.findChallengeInvitationCode(any(), any()) } returns dto

        // when
        val resultActions = mockMvc.perform(
            get("/api/challenges/{challengeId}/invitation-code", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    private fun getCreateChallengeRequest(): CreateChallengeRequest {
        return CreateChallengeRequest(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.now().plusDays(1),
            listOf(
                ChallengeRuleRequest("챌린지 인증 룰1"),
                ChallengeRuleRequest("챌린지 인증 룰2"),
                ChallengeRuleRequest("챌린지 인증 룰3"),
            ),
            listOf(
                ChallengeHashtagRequest("해시태그 1"),
                ChallengeHashtagRequest("해시태그 2"),
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
            listOf(
                FindChallengeHashtagDto(1L, "해시태그 1"),
                FindChallengeHashtagDto(1L, "해시태그 2"),
            )
        )
    }

    private fun getFindPopularChallengesDto(): FindPopularChallengesDto {
        return FindPopularChallengesDto(
            1L,
            "챌린지 이름",
            "https://url.kr/5MhHhD",
            "챌린지 목표입니다.",
            3,
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            listOf(
                FindChallengeHashtagDto(1L, "해시태그 1"),
                FindChallengeHashtagDto(1L, "해시태그 2"),
            ),
            listOf("https://url.kr/5MhHhD", "https://url.kr/5MhHhD", "https://url.kr/5MhHhD")
        )
    }

    private fun getFindChallengeDto(): FindChallengeDto {
        return FindChallengeDto(
            "챌린지 이름",
            "챌린지 목표입니다.",
            "https://url.kr/5MhHhD",
            5,
            true,
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            listOf(
                ChallengeRuleDto("챌린지 인증 룰1"),
                ChallengeRuleDto("챌린지 인증 룰2"),
                ChallengeRuleDto("챌린지 인증 룰3"),
            ),
            listOf(
                ChallengeHashtagDto("해시태그 1"),
                ChallengeHashtagDto("해시태그 2"),
            ),
            listOf(
                ChallengeMemberImageDto("https://url.kr/5MhHhD"),
                ChallengeMemberImageDto("https://url.kr/5MhHhD"),
                ChallengeMemberImageDto("https://url.kr/5MhHhD"),
            )
        )
    }

    private fun getUpdateChallengeRequest(): UpdateChallengeRequest {
        return UpdateChallengeRequest(
            "챌린지 이름",
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.now().plusDays(1),
            listOf(
                ChallengeRuleRequest("챌린지 인증 룰1"),
                ChallengeRuleRequest("챌린지 인증 룰2"),
                ChallengeRuleRequest("챌린지 인증 룰3"),
            ),
            listOf(
                ChallengeHashtagRequest("해시태그 1"),
                ChallengeHashtagRequest("해시태그 2"),
            )
        )
    }

    private fun getFindChallengeFeedsDto(): FindChallengeFeedsDto {
        return FindChallengeFeedsDto(
            1L,
            "tester",
            "https://url.kr/5MhHhD",
            LocalDateTime.now(),
            LocalTime.of(13, 0),
            true,
        )
    }

    private fun getFindChallengeFeedDto(): FindChallengeFeedDto {
        return FindChallengeFeedDto(
            "tester",
            "https://url.kr/5MhHhD",
            "https://url.kr/5MhHhD",
            LocalDateTime.now(),
            10,
            true,
        )
    }
}