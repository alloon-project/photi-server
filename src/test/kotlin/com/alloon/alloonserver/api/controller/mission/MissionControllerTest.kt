package com.alloon.alloonserver.api.controller.mission

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.mission.request.MissionCreateHashTagRequest
import com.alloon.alloonserver.api.controller.mission.request.MissionCreateMissionRuleRequest
import com.alloon.alloonserver.api.controller.mission.request.MissionCreateRequest
import com.alloon.alloonserver.service.mission.MissionService
import com.alloon.alloonserver.service.mission.response.MissionCreateCreatorResponse
import com.alloon.alloonserver.service.mission.response.MissionCreateResponse
import com.alloon.alloonserver.service.mission.response.MissionCreateRuleResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

class MissionControllerTest : RestDocsSupport() {

    private val missionService = mock(MissionService::class.java)

    override fun initController(): Any {
        return MissionController(missionService)
    }

    @DisplayName("미션 예시 이미지 전체 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllMissionTemplateImages_thenReturn200() {
        // given
        `when`(missionService.getAllMissionTemplateImages(any()))
            .thenReturn(listOf("https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png"))

        // when & then
        mockMvc.perform(
            get("/api/missions/image/templates")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        ).andDo(print()).andExpect(status().isOk)
    }

    @DisplayName("미션 생성을 하면 201을 반환한다")
    @Test
    fun givenValid_whenCreateMission_thenReturn201() {
        // given
        val request = createValidMissionCreateRequest()
        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(
                MissionCreateResponse(
                    1L,
                    request.missionName,
                    request.missionDescription,
                    request.missionGoal,
                    request.missionRules.map { MissionCreateRuleResponse(1L, it.missionRule) },
                    request.missionImageUrl,
                    1,
                    MissionCreateCreatorResponse("tester", ""),
                    now,
                    request.missionEndDate
                )
            )

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print()).andExpect(status().isCreated)
    }

    @DisplayName("미션명 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankMissionName_whenCreateMission_thenReturn400() {
        // given
        val request = MissionCreateRequest(
            "",
            "얼른 프로젝트 설명입니다.",
            "얼른 프로젝트 목표입니다.",
            listOf(MissionCreateMissionRuleRequest("얼른 프로젝트 인증 룰 입니다.")),
            "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            LocalDate.of(2025, 1, 1),
            listOf(MissionCreateHashTagRequest("해시"), MissionCreateHashTagRequest("태그"))
        )

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName,
                request.missionDescription,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it.missionRule) },
                request.missionImageUrl,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                request.missionEndDate
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print()).andExpect(status().isBadRequest)
    }

    @DisplayName("미션 소개 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankMissionDescription_whenCreateMission_thenReturn400() {
        // given
        val request = MissionCreateRequest(
            "얼른",
            "",
            "얼른 프로젝트 목표입니다.",
            listOf(MissionCreateMissionRuleRequest("얼른 프로젝트 인증 룰 입니다.")),
            "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            LocalDate.of(2025, 1, 1),
            listOf(MissionCreateHashTagRequest("해시"), MissionCreateHashTagRequest("태그"))
        )

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName,
                request.missionDescription,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it.missionRule) },
                request.missionImageUrl,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                request.missionEndDate
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print()).andExpect(status().isBadRequest)
    }

    @DisplayName("미션 목표 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankMissionGoal_whenCreateMission_thenReturn400() {
        // given
        val request = MissionCreateRequest(
            "얼른",
            "얼른 프로젝트 설명입니다.",
            "",
            listOf(MissionCreateMissionRuleRequest("얼른 프로젝트 인증 룰 입니다.")),
            "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            LocalDate.of(2025, 1, 1),
            listOf(MissionCreateHashTagRequest("해시"), MissionCreateHashTagRequest("태그"))
        )

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName,
                request.missionDescription,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it.missionRule) },
                request.missionImageUrl,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                request.missionEndDate
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print()).andExpect(status().isBadRequest)
    }

    @DisplayName("미션 대표 이미지 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankMissionImageUrl_whenCreateMission_thenReturn400() {
        // given
        val request = MissionCreateRequest(
            "얼른",
            "얼른 프로젝트 설명입니다.",
            "얼른 프로젝트 목표입니다.",
            listOf(MissionCreateMissionRuleRequest("얼른 프로젝트 인증 룰 입니다.")),
            "",
            LocalDate.of(2025, 1, 1),
            listOf(MissionCreateHashTagRequest("해시"), MissionCreateHashTagRequest("태그"))
        )

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName,
                request.missionDescription,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it.missionRule) },
                request.missionImageUrl,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                now
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print()).andExpect(status().isBadRequest)
    }

    @DisplayName("미션 이미지 업로드를 하면 200을 반환한다")
    @Test
    fun givenValid_whenUploadMissionImage_thenReturn200() {
        // given
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        `when`(missionService.uploadMissionImage(anyLong(), any()))
            .thenReturn("https://www.google.com")

        // when & then
        mockMvc.perform(
            post("/api/missions/image")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .param("file", file.toString())
        ).andDo(print()).andExpect(status().isOk)
    }

    private fun createValidMissionCreateRequest(): MissionCreateRequest {
        return MissionCreateRequest(
            "얼른",
            "얼른 프로젝트 설명입니다.",
            "얼른 프로젝트 목표입니다.",
            listOf(MissionCreateMissionRuleRequest("얼른 프로젝트 인증 룰 입니다.")),
            "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            LocalDate.of(2025, 1, 1),
            listOf(MissionCreateHashTagRequest("해시"), MissionCreateHashTagRequest("태그"))
        )
    }
}