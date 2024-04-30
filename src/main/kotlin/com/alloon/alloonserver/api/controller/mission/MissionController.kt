package com.alloon.alloonserver.api.controller.mission

import com.alloon.alloonserver.api.controller.mission.request.MissionCreateRequest
import com.alloon.alloonserver.api.service.mission.MissionService
import com.alloon.alloonserver.api.service.mission.response.MissionGetAllMissionTemplateImagesResponse
import com.alloon.alloonserver.common.constant.SuccessCode.FOUND_MISSION_TEMPLATE_IMAGES
import com.alloon.alloonserver.common.constant.SuccessCode.MISSION_CREATED
import com.alloon.alloonserver.common.response.DefaultMultiResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.time.LocalDateTime

@Validated
@RestController
class MissionController(
    private val missionService: MissionService,
) {

    @GetMapping("/api/v1/missions/image/templates")
    fun getAllMissionTemplateImages(): ResponseEntity<DefaultMultiResponse<String>> {
        val response = missionService.getAllMissionTemplateImages(LocalDateTime.now())

        return DefaultMultiResponse.toResponseEntity(FOUND_MISSION_TEMPLATE_IMAGES, response)
    }

    @PostMapping("/api/v1/missions")
    fun createMission(principal: Principal, @RequestBody @Valid request: MissionCreateRequest):
            ResponseEntity<DefaultSingleResponse> {
        val response = missionService.createMission(UserUtility.getUserId(principal), request.toServiceRequest())

        return DefaultSingleResponse.toResponseEntity(MISSION_CREATED, response)
    }
}