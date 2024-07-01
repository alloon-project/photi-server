package com.alloon.alloonserver.api.controller.mission

import com.alloon.alloonserver.api.controller.mission.request.MissionCreateRequest
import com.alloon.alloonserver.api.service.mission.MissionService
import com.alloon.alloonserver.api.service.mission.request.MissionServiceCreateMissionRequest
import com.alloon.alloonserver.common.constant.SuccessCode.*
import com.alloon.alloonserver.common.response.DefaultListResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import io.swagger.v3.oas.annotations.Operation
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
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "챌린지 예시 이미지 리스트 조회 성공"),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
        ]
    )
    fun getAllMissionTemplateImages(): ResponseEntity<DefaultListResponse<String>> {
        val response = missionService.getAllMissionTemplateImages(LocalDateTime.now())

        return DefaultListResponse.toResponseEntity(FOUND_MISSION_TEMPLATE_IMAGES, response)
    }

    @PostMapping("/api/missions")
    @Operation(summary = "챌린지 생성")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "챌린지 생성 성공"),
            ApiResponse(
                responseCode = "400",
                description = """
                1. 미션명은 필수 입력입니다.
                2. 미션명은 2~16자만 가능합니다.
                3. 미션 소개는 필수 입력입니다.
                4. 미션 소개는 10~120자만 가능합니다.
                5. 규칙은 0~5개만 가능합니다.
                6. 규칙은 1~30자만 가능합니다.
                7. 목표는 필수 입력입니다.
                8. 목표는 1~30자만 가능합니다.
                9. 미션 대표 이미지는 필수 입력입니다.
                10. 미션 대표 이미지는 1~500자만 가능합니다.
                11. 미션 종료일은 필수 입력입니다.
                12. 해시태그는 1~5개만 가능합니다.
                13. 해시태그는 1~5자만 가능합니다.
                """
            ),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
            ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.")
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
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "챌린지 이미지 업로드 성공"),
            ApiResponse(responseCode = "400", description = "파일은 필수 입력입니다."),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
            ApiResponse(responseCode = "415", description = "이미지는 '.jpeg', '.jpg', 또는 '.png'만 가능합니다."),
        ]
    )
    fun uploadMissionImage(
        principal: Principal,
        @RequestPart(required = false) file: MultipartFile?
    ): ResponseEntity<DefaultSingleResponse> {
        val response = missionService.uploadMissionImage(UserUtility.getUserId(principal), file)

        return DefaultSingleResponse.toResponseEntity(MISSION_IMAGE_UPLOADED, response)
    }
}