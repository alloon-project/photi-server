package com.alloon.alloonserver.api.controller.mission

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.mission.request.MissionCreateRequest
import com.alloon.alloonserver.api.service.mission.MissionService
import com.alloon.alloonserver.api.service.mission.response.MissionCreateCreatorResponse
import com.alloon.alloonserver.api.service.mission.response.MissionCreateHashtagResponse
import com.alloon.alloonserver.api.service.mission.response.MissionCreateResponse
import com.alloon.alloonserver.api.service.mission.response.MissionCreateRuleResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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
            get("/api/missions/image/templates")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "mission/get-all-mission-template-images",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY)
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
                request.missionName!!,
                request.missionDescription!!,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it) },
                request.missionImageUrl!!,
                1,
                MissionCreateCreatorResponse("tester", ""),
                now,
                request.missionEndDate!!,
                request.hashtags!!.map { MissionCreateHashtagResponse(1L, it) }
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isCreated)
            .andDo(
                document(
                    "mission/create-mission",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .description("목표"),
                        fieldWithPath("missionRules").type(JsonFieldType.ARRAY).optional()
                            .description("규칙"),
                        fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        fieldWithPath("missionEndDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
                            .description("해시태그")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.missionId").type(JsonFieldType.NUMBER)
                            .description("미션 식별자"),
                        fieldWithPath("data.missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        fieldWithPath("data.rules").type(JsonFieldType.ARRAY).optional()
                            .description("미션 규칙들"),
                        fieldWithPath("data.rules[].missionRuleId").type(JsonFieldType.NUMBER).optional()
                            .description("미션 규칙 식별자"),
                        fieldWithPath("data.rules[].rule").type(JsonFieldType.STRING).optional()
                            .description("규칙"),
                        fieldWithPath("data.goal").type(JsonFieldType.STRING)
                            .description("목표"),
                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                            .description("미션 대표 이미지"),
                        fieldWithPath("data.currentMemberCnt").type(JsonFieldType.NUMBER)
                            .description("현재 멤버 인원"),
                        fieldWithPath("data.missionCreator").type(JsonFieldType.OBJECT)
                            .description("미션 설립자"),
                        fieldWithPath("data.missionCreator.username").type(JsonFieldType.STRING)
                            .description("미션 설립자 아이디"),
                        fieldWithPath("data.missionCreator.imageUrl").type(JsonFieldType.STRING)
                            .description("미션 설립자 프로필 이미지"),
                        fieldWithPath("data.startDate").type(JsonFieldType.STRING)
                            .description("미션 시작일"),
                        fieldWithPath("data.endDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        fieldWithPath("data.hashtags").type(JsonFieldType.ARRAY)
                            .description("해시태그들"),
                        fieldWithPath("data.hashtags[].hashtagId").type(JsonFieldType.NUMBER)
                            .description("해시태그 식별자"),
                        fieldWithPath("data.hashtags[].tag").type(JsonFieldType.STRING)
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
                request.missionName!!,
                request.missionDescription!!,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it) },
                request.missionImageUrl!!,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                request.missionEndDate!!,
                request.hashtags!!.map { MissionCreateHashtagResponse(1L, it) }
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "mission/create-mission/mission-name-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .description("목표"),
                        fieldWithPath("missionRules").type(JsonFieldType.ARRAY).optional()
                            .description("규칙"),
                        fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        fieldWithPath("missionEndDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
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
                request.missionName!!,
                request.missionDescription!!,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it) },
                request.missionImageUrl!!,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                request.missionEndDate!!,
                request.hashtags!!.map { MissionCreateHashtagResponse(1L, it) }
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "mission/create-mission/mission-description-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .description("목표"),
                        fieldWithPath("missionRules").type(JsonFieldType.ARRAY).optional()
                            .description("규칙"),
                        fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        fieldWithPath("missionEndDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
                            .description("해시태그")
                    )
                )
            )
    }

    @DisplayName("미션 목표 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankMissionGoal_whenCreateMission_thenReturn400() {
        // given
        val request = createValidMissionCreateRequest()
        request.missionGoal = ""

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName!!,
                request.missionDescription!!,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it) },
                request.missionImageUrl!!,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                request.missionEndDate!!,
                request.hashtags!!.map { MissionCreateHashtagResponse(1L, it) }
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "mission/create-mission/mission-goal-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .description("목표"),
                        fieldWithPath("missionRules").type(JsonFieldType.ARRAY).optional()
                            .description("규칙"),
                        fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        fieldWithPath("missionEndDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
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
                request.missionName!!,
                request.missionDescription!!,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it) },
                request.missionImageUrl!!,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                now,
                request.hashtags!!.map { MissionCreateHashtagResponse(1L, it) }
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "mission/create-mission/mission-image_url-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .description("목표"),
                        fieldWithPath("missionRules").type(JsonFieldType.ARRAY).optional()
                            .description("규칙"),
                        fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        fieldWithPath("missionEndDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
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
                request.missionName!!,
                request.missionDescription!!,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it) },
                request.missionImageUrl!!,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                now,
                request.hashtags!!.map { MissionCreateHashtagResponse(1L, it) }
            ))

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "mission/create-mission/mission-end-date-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .description("목표"),
                        fieldWithPath("missionRules").type(JsonFieldType.ARRAY).optional()
                            .description("규칙"),
                        fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        fieldWithPath("missionEndDate").type(JsonFieldType.STRING).optional()
                            .description("미션 종료일"),
                        fieldWithPath("hashtags").type(JsonFieldType.ARRAY)
                            .description("해시태그")
                    )
                )
            )
    }

    @DisplayName("해시태그 미입력시 미션 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankHashtags_whenCreateMission_thenReturn400() {
        // given
        val request = createValidMissionCreateRequest()

        val now = LocalDate.now()

        `when`(missionService.createMission(anyLong(), any()))
            .thenReturn(MissionCreateResponse(
                1L,
                request.missionName!!,
                request.missionDescription!!,
                request.missionGoal,
                request.missionRules.map { MissionCreateRuleResponse(1L, it) },
                request.missionImageUrl!!,
                1,
                MissionCreateCreatorResponse("tester", null),
                now,
                now,
                request.hashtags!!.map { MissionCreateHashtagResponse(1L, it) }
            ))

        request.hashtags = null

        // when & then
        mockMvc.perform(
            post("/api/missions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "mission/create-mission/hashtags-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("missionName").type(JsonFieldType.STRING)
                            .description("미션명"),
                        fieldWithPath("missionDescription").type(JsonFieldType.STRING)
                            .description("미션 소개"),
                        fieldWithPath("missionGoal").type(JsonFieldType.STRING)
                            .description("목표"),
                        fieldWithPath("missionRules").type(JsonFieldType.ARRAY).optional()
                            .description("규칙"),
                        fieldWithPath("missionImageUrl").type(JsonFieldType.STRING)
                            .description("대표 이미지"),
                        fieldWithPath("missionEndDate").type(JsonFieldType.STRING)
                            .description("미션 종료일"),
                        fieldWithPath("hashtags").type(JsonFieldType.ARRAY).optional()
                            .description("해시태그")
                    )
                )
            )
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
        ).andDo(print())
                .andExpect(status().isOk)
                .andDo(
                        document(
                                "mission/upload-mission-image",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.STRING)
                                                .description("코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("메세지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING)
                                                .description("데이터")
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