package com.alloon.alloonserver.api.controller.mission

import com.alloon.alloonserver.api.controller.mission.request.MissionCreateRequest
import com.alloon.alloonserver.api.service.mission.MissionService
import com.alloon.alloonserver.common.constant.SuccessCode.MISSION_IMAGE_UPLOADED
import com.alloon.alloonserver.common.constant.SuccessCode.FOUND_MISSION_TEMPLATE_IMAGES
import com.alloon.alloonserver.common.constant.SuccessCode.MISSION_CREATED
import com.alloon.alloonserver.common.response.DefaultListResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
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
class MissionController(
    private val missionService: MissionService,
) {

    /**
     * 200 상태코드와 모든 챌린지 템플릿 이미지 리스트를 포함한 응답을 반환한다.
     *
     * @return [FOUND_MISSION_TEMPLATE_IMAGES] 및 모든 챌린지 템플릿 이미지 리스트를 포함한 응답
     */
    @GetMapping("/api/missions/image/templates")
    fun getAllMissionTemplateImages(): ResponseEntity<DefaultListResponse<String>> {
        val response = missionService.getAllMissionTemplateImages(LocalDateTime.now())

        return DefaultListResponse.toResponseEntity(FOUND_MISSION_TEMPLATE_IMAGES, response)
    }

    /**
     * 201 상태코드와 생성된 챌린지 정보를 포함한 응답을 반환한다.
     *
     * @param principal 사용자 인증 정보
     * @param request 챌린지 생성 폼 데이터
     * @return [MISSION_CREATED] 및 생성된 챌린지 정보를 포함한 응답
     */
    @PostMapping("/api/missions")
    fun createMission(
        principal: Principal,
        @RequestBody @Valid request: MissionCreateRequest
    ): ResponseEntity<DefaultSingleResponse> {
        val response = missionService.createMission(UserUtility.getUserId(principal), request.toServiceRequest())

        return DefaultSingleResponse.toResponseEntity(MISSION_CREATED, response)
    }

    /**
     * 200 상태코드와 업로드된 이미지 정보를 포함한 응답을 반환한다.
     *
     * @param principal 사용자 인증 정보
     * @param file 업로드할 이미지 파일 (선택 사항)
     * @return [MISSION_IMAGE_UPLOADED] 및 업로드된 이미지 정보를 포함한 응답
     */
    @PostMapping(
        "/api/missions/image",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun uploadMissionImage(
        principal: Principal,
        @RequestPart(required = false) file: MultipartFile?
    ): ResponseEntity<DefaultSingleResponse> {
        val response = missionService.uploadMissionImage(UserUtility.getUserId(principal), file)

        return DefaultSingleResponse.toResponseEntity(MISSION_IMAGE_UPLOADED, response)
    }
}