package com.alloon.alloonserver.api.controller.develop

import com.alloon.alloonserver.api.service.develop.DevelopService
import com.alloon.alloonserver.common.constant.SuccessCode.SERVER_OK
import com.alloon.alloonserver.common.response.DefaultResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DevelopController(
    private val developService: DevelopService
) {

    @GetMapping("/api/v1/health")
    fun healthCheck(): ResponseEntity<DefaultResponse> {
        val serverName = developService.getServerName()

        return ResponseEntity.status(SERVER_OK.httpStatus)
            .body(DefaultResponse(SERVER_OK.name, serverName + " " + SERVER_OK.message))
    }
}