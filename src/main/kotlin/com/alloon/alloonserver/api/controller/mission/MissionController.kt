package com.alloon.alloonserver.api.controller.mission

import com.alloon.alloonserver.api.controller.mission.request.MissionCreateRequest
import com.alloon.alloonserver.api.service.mission.MissionService
import com.alloon.alloonserver.common.constant.SuccessCode.MISSION_CREATED
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Validated
@RestController
class MissionController(
    private val missionService: MissionService,
) {

    @PostMapping("/api/v1/missions")
    fun createMission(principal: Principal, @RequestBody @Valid request: MissionCreateRequest):
            ResponseEntity<DefaultSingleResponse> {
        val response = missionService.createMission(UserUtility.getUserId(principal), request.toServiceRequest())

        return ResponseEntity.status(MISSION_CREATED.httpStatus)
            .body(DefaultSingleResponse(MISSION_CREATED.name, MISSION_CREATED.message, response))
    }
}