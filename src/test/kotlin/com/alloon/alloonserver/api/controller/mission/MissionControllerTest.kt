package com.alloon.alloonserver.api.controller.mission

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.mission.request.CreateMissionHashtagRequest
import com.alloon.alloonserver.api.controller.mission.request.CreateMissionRequest
import com.alloon.alloonserver.api.controller.mission.request.CreateMissionRuleRequest
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.service.mission.dto.FindPopularMissionsDto
import com.alloon.alloonserver.service.mission.MissionService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalTime

class MissionControllerTest : RestDocsSupport() {

    private val missionService = mockk<MissionService>()

    override fun initController(): Any {
        return MissionController(missionService)
    }

    @DisplayName("미션 예시 이미지 전체 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllMissionTemplateImages_thenReturn200() {
        // given
        every { missionService.getAllMissionTemplateImages(any()) } returns listOf("https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png")

        // when
        val resultActions = mockMvc.perform(
            get("/api/missions/image/templates")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("미션 생성을 하면 201을 반환한다")
    @Test
    fun givenValid_whenCreateMission_thenReturn201() {
        // given
        val request = getCreateMissionRequest()
        val mission = Mission.toEntity(request.toServiceDto())

        every { missionService.createMission(any(), any()) } returns mission

        // when
        val resultActions = mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("미션 생성이 완료되었습니다."))
    }

    @DisplayName("미션 이미지 업로드를 하면 200을 반환한다")
    @Test
    fun givenValid_whenUploadMissionImage_thenReturn200() {
        // given
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        every { missionService.uploadMissionImage(any(), any()) } returns "https://www.google.com"

        // when
        val resultActions = mockMvc.perform(
            post("/api/missions/image")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .param("file", file.toString())
        )

        // then
        resultActions.andExpect(status().isOk)
    }

    @DisplayName("챌린지가 있는 경우 지금 인기있는 챌린지 조회를 하면 200을 반환한다")
    @Test
    fun givenMission_whenFindPopularMissions_thenReturn200() {
        // given
        val mission = FindPopularMissionsDto(
            1L,
            "챌린지 이름",
            LocalDate.of(2024, 12, 1),
            "https://url.kr/5MhHhD",
            listOf("해시태그 1", "해시태그 2")
        )

        every { missionService.findPopularMissions() } returns listOf(mission)

        // when
        val resultActions = mockMvc.perform(
            get("/api/missions/popular")
        )

        // then
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("지금 인기있는 챌린지를 전체 조회했습니다."))
    }

    @DisplayName("챌린지가 없는 경우 지금 인기있는 챌린지 조회를 하면 200을 반환한다")
    @Test
    fun givenNoMission_whenFindPopularMissions_thenReturn200() {
        // given
        every { missionService.findPopularMissions() } returns listOf()

        // when
        val resultActions = mockMvc.perform(
            get("/api/missions/popular")
                .param("size", "5")
                .param("sort", "visitCnt,DESC")
        )

        // then
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("지금 인기있는 챌린지가 없습니다."))
    }

    private fun getCreateMissionRequest(): CreateMissionRequest {
        return CreateMissionRequest(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            "https://url.kr/5MhHhD",
            listOf(
                CreateMissionRuleRequest("챌린지 인증 룰1"),
                CreateMissionRuleRequest("챌린지 인증 룰2"),
                CreateMissionRuleRequest("챌린지 인증 룰3"),
            ),
            listOf(
                CreateMissionHashtagRequest("해시태그 1"),
                CreateMissionHashtagRequest("해시태그 2"),
            )
        )
    }
}