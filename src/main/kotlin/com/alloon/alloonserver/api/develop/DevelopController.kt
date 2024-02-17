package com.alloon.alloonserver.api.develop

import com.alloon.alloonserver.common.constant.SuccessCode.SERVER_OK
import com.alloon.alloonserver.common.response.DefaultResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DevelopController {

    @GetMapping("/api/v1/health")
    fun healthCheck(): ResponseEntity<DefaultResponse> {
        return ResponseEntity.status(SERVER_OK.httpStatus)
            .body(DefaultResponse(SERVER_OK.name, SERVER_OK.message))
    }
}