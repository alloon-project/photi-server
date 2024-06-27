package com.alloon.alloonserver.api.controller.mission

import com.alloon.alloonserver.api.controller.mission.request.MissionCreateRequest
import com.alloon.alloonserver.api.service.mission.MissionService
import com.alloon.alloonserver.api.service.mission.request.MissionServiceCreateMissionRequest
import com.alloon.alloonserver.api.service.mission.response.MissionCreateResponse
import com.alloon.alloonserver.common.constant.SuccessCode.*
import com.alloon.alloonserver.common.response.DefaultListResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.Principal
import java.time.LocalDateTime

@Validated
@RestController
@Tag(name = "Challenge", description = "챌린지 API")
class MissionController(
    private val missionService: MissionService,
) {

    @GetMapping("/api/missions/image/templates")
    @Operation(summary = "챌린지 예시 이미지 리스트 조회")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "챌린지 예시 이미지 리스트 조회 성공")])
    fun getAllMissionTemplateImages(): ResponseEntity<DefaultListResponse<String>> {
        val response = missionService.getAllMissionTemplateImages(LocalDateTime.now())

        return DefaultListResponse.toResponseEntity(FOUND_MISSION_TEMPLATE_IMAGES, response)
    }

    @PostMapping("/api/missions")
    @Operation(summary = "챌린지 생성")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "챌린지 생성 성공",
                content = [Content(schema = Schema(implementation = MissionCreateResponse::class))]
            )
        ]
    )
    fun createMission(
        principal: Principal,
        @RequestBody @Valid @Schema(implementation = MissionServiceCreateMissionRequest::class)
        request: MissionCreateRequest
    ): ResponseEntity<DefaultSingleResponse> {
        val response = missionService.createMission(UserUtility.getUserId(principal), request.toServiceRequest())

        return DefaultSingleResponse.toResponseEntity(MISSION_CREATED, response)
    }

    @PostMapping(
        "/api/missions/image",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @Operation(summary = "챌린지 이미지 업로드")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "챌린지 이미지 업로드 성공")])
    fun uploadMissionImage(
        principal: Principal,
        @RequestPart(required = false) file: MultipartFile?
    ): ResponseEntity<DefaultSingleResponse> {
        val response = missionService.uploadMissionImage(UserUtility.getUserId(principal), file)

        return DefaultSingleResponse.toResponseEntity(MISSION_IMAGE_UPLOADED, response)
    }
}