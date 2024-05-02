package com.alloon.alloonserver.api.controller.develop

import com.alloon.alloonserver.api.service.develop.DevelopService
import com.alloon.alloonserver.common.constant.SuccessCode.FORCE_UPDATE
import com.alloon.alloonserver.common.constant.SuccessCode.SERVER_OK
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class DevelopController(
    private val developService: DevelopService
) {

    @GetMapping("/api/health")
    fun healthCheck(): ResponseEntity<DefaultResponse> {
        val serverName = developService.getServerName()

        return DefaultResponse.toResponseEntity(SERVER_OK)
    }

    @GetMapping("/api/ver")
    fun needForceUpdate(@RequestParam("version") @NotBlank(message = "앱 버전은 필수 입력입니다.") version: String):
            ResponseEntity<DefaultSingleResponse>  {
        val response = developService.needForceUpdate(version)

        return DefaultSingleResponse.toResponseEntity(FORCE_UPDATE, response)
    }
}