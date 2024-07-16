package com.alloon.alloonserver.api.controller.develop

import com.alloon.alloonserver.common.constant.SuccessCode.FORCE_UPDATE
import com.alloon.alloonserver.common.constant.SuccessCode.SERVER_OK
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.service.develop.DevelopService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@Tag(name = "Develop", description = "서버, 앱 버전 API")
class DevelopController(
    private val developService: DevelopService,
) {

    @GetMapping("/api/health")
    @Operation(summary = "서버 헬스 체크", description = "해당 서버가 현재 서비스 가능한 상태인지 점검합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "서버 헬스 체크 성공")])
    fun healthCheck(): ResponseEntity<DefaultResponse> {
        val serverName = developService.getServerName()

        return ResponseEntity.status(SERVER_OK.httpStatus)
            .body(DefaultResponse(SERVER_OK.name, serverName + SERVER_OK.message))
    }

    @GetMapping("/api/ver")
    @Operation(summary = "앱 강제 업데이트 필요 여부 조회", description = "사용자의 앱 버전을 확인하고, 앱 업데이트가 필요한지 조회합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "앱 강제 업데이트 필요 여부 조회 성공")])
    fun needForceUpdate(
        @RequestParam("version") @NotBlank(message = "앱 버전은 필수 입력입니다.")
        @Parameter(description = "앱 버전", example = "1.0.0")
        version: String
    ): ResponseEntity<DefaultSingleResponse> {
        val response = developService.needForceUpdate(version)

        return DefaultSingleResponse.toResponseEntity(FORCE_UPDATE, response)
    }
}