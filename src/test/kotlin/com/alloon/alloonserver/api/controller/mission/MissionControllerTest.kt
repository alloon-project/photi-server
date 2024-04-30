package com.alloon.alloonserver.api.controller.mission

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.mission.request.MissionCreateRequest
import com.alloon.alloonserver.api.service.mission.MissionService
import com.alloon.alloonserver.api.service.mission.response.MissionCreateCreatorResponse
import com.alloon.alloonserver.api.service.mission.response.MissionCreateResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

class MissionControllerTest: RestDocsSupport() {

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
            MockMvcRequestBuilders.get("/api/v1/missions/image/templates")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "mission/get-all-mission-template-images",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        PayloadDocumentation.fieldWithPath("total").type(JsonFieldType.NUMBER)
                            .description("데이터 총 개수"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.ARRAY)
                            .description("데이터"),
                    )
                )
            )
    }

    @DisplayName("미션 생성을 하면 201을 반환한다")
    @Test
    fun givenValid_whenCreateMission_thenReturn201() {
        // given
        val request = createValidMissionCreateRequest()
        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName,
                request.missionDescription,
                request.missionGoal,
                request.missionRules,
                request.missionImageUrl,
                1,
                MissionCreateCreatorResponse("tester", ""),
                now,
                request.missionEndDate!!,
                request.hashtags
            ))

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isCreated)
            .andDo(
                MockMvcRestDocumentation.document(
                    "mission/create-mission",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        PayloadDocumentation.fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        PayloadDocumentation.fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .optional()
                            .description("목표"),
                        PayloadDocumentation.fieldWithPath("missionRules").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("규칙"),
                        PayloadDocumentation.fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .optional()
                            .description("대표 이미지"),
                        PayloadDocumentation.fieldWithPath("missionEndDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        PayloadDocumentation.fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("해시태그")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        PayloadDocumentation.fieldWithPath("data.missionId").type(JsonFieldType.NUMBER)
                            .description("미션 식별자"),
                        PayloadDocumentation.fieldWithPath("data.missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        PayloadDocumentation.fieldWithPath("data.description").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        PayloadDocumentation.fieldWithPath("data.rules").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("규칙"),
                        PayloadDocumentation.fieldWithPath("data.goal").type(JsonFieldType.STRING)
                            .optional()
                            .description("목표"),
                        PayloadDocumentation.fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                            .description("미션 대표 이미지"),
                        PayloadDocumentation.fieldWithPath("data.currentMemberCnt").type(JsonFieldType.NUMBER)
                            .description("현재 멤버 인원"),
                        PayloadDocumentation.fieldWithPath("data.missionCreator").type(JsonFieldType.OBJECT)
                            .description("미션 설립자"),
                        PayloadDocumentation.fieldWithPath("data.missionCreator.username").type(JsonFieldType.STRING)
                            .description("미션 설립자 아이디"),
                        PayloadDocumentation.fieldWithPath("data.missionCreator.imageUrl").type(JsonFieldType.STRING)
                            .description("미션 설립자 프로필 이미지"),
                        PayloadDocumentation.fieldWithPath("data.startDate").type(JsonFieldType.STRING)
                            .description("미션 시작일"),
                        PayloadDocumentation.fieldWithPath("data.endDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        PayloadDocumentation.fieldWithPath("data.hashtags").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("해시태그"),
                    )
                )
            )
    }

    @DisplayName("미션명 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankMissionName_whenCreateMission_thenReturn400() {
        // given
        val request = createValidMissionCreateRequest()
        request.missionName = ""

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName,
                request.missionDescription,
                request.missionGoal,
                request.missionRules,
                request.missionImageUrl,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                request.missionEndDate!!,
                request.hashtags
            ))

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "mission/create-mission/mission-name-filed-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        PayloadDocumentation.fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        PayloadDocumentation.fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .optional()
                            .description("목표"),
                        PayloadDocumentation.fieldWithPath("missionRules").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("규칙"),
                        PayloadDocumentation.fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        PayloadDocumentation.fieldWithPath("missionEndDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        PayloadDocumentation.fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("해시태그")
                    )
                )
            )
    }

    @DisplayName("미션 소개 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankMissionDescription_whenCreateMission_thenReturn400() {
        // given
        val request = createValidMissionCreateRequest()
        request.missionDescription = ""

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName,
                request.missionDescription,
                request.missionGoal,
                request.missionRules,
                request.missionImageUrl,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                request.missionEndDate!!,
                request.hashtags
            ))

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "mission/create-mission/mission-name-filed-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        PayloadDocumentation.fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        PayloadDocumentation.fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .optional()
                            .description("목표"),
                        PayloadDocumentation.fieldWithPath("missionRules").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("규칙"),
                        PayloadDocumentation.fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        PayloadDocumentation.fieldWithPath("missionEndDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        PayloadDocumentation.fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("해시태그")
                    )
                )
            )
    }

    @DisplayName("미션 대표 이미지 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankMissionImageUrl_whenCreateMission_thenReturn400() {
        // given
        val request = createValidMissionCreateRequest()
        request.missionImageUrl = ""

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName,
                request.missionDescription,
                request.missionGoal,
                request.missionRules,
                request.missionImageUrl,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                now,
                request.hashtags
            ))

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "mission/create-mission/mission-image_url-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        PayloadDocumentation.fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        PayloadDocumentation.fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .optional()
                            .description("목표"),
                        PayloadDocumentation.fieldWithPath("missionRules").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("규칙"),
                        PayloadDocumentation.fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        PayloadDocumentation.fieldWithPath("missionEndDate").type(JsonFieldType.STRING).optional()
                            .description("미션 종료일"),
                        PayloadDocumentation.fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("해시태그")
                    )
                )
            )
    }

    @DisplayName("미션 종료일 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankMissionEndDate_whenCreateMission_thenReturn400() {
        // given
        val request = createValidMissionCreateRequest()
        request.missionEndDate = null

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName,
                request.missionDescription,
                request.missionGoal,
                request.missionRules,
                request.missionImageUrl,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                now,
                request.hashtags
            ))

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "mission/create-mission/mission-end-date-field-required",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        PayloadDocumentation.fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        PayloadDocumentation.fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .optional()
                            .description("목표"),
                        PayloadDocumentation.fieldWithPath("missionRules").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("규칙"),
                        PayloadDocumentation.fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        PayloadDocumentation.fieldWithPath("missionEndDate").type(JsonFieldType.STRING).optional()
                            .description("미션 종료일"),
                        PayloadDocumentation.fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
                            .optional()
                            .description("해시태그")
                    )
                )
            )
    }

    private fun createValidMissionCreateRequest(): MissionCreateRequest {
        return MissionCreateRequest("얼른", "얼른 프로젝트 설명입니다.",
            "얼른 프로젝트 목표입니다.", listOf("얼른 프로젝트 규칙입니다."),
            "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            LocalDate.of(2025, 1, 1), listOf("해시", "태그"))
    }
}