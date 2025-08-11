package com.photi.server.api.controller.appversion

import com.photi.server.api.controller.appversion.request.AppVersionRequest
import com.photi.server.api.controller.appversion.response.AppVersionResponse
import com.photi.server.common.constant.ExceptionCode
import com.photi.server.common.response.ApiErrorResponses
import com.photi.server.service.appversion.AppVersionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/app-version")
@Tag(name = "AppVersion", description = "앱 버전 API")
class AppVersionController(
    private val appVersionService: AppVersionService,
) {

    @PostMapping
    @Operation(summary = "앱 강제 업데이트 필요 여부 조회")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.OS_POLICY_NOT_FOUND])
    fun checkAppVersion(@RequestBody @Valid request: AppVersionRequest): ResponseEntity<AppVersionResponse> {
        val forceUpdate = appVersionService.checkAppVersion(request.toServiceDto())
        val response = AppVersionResponse.of(forceUpdate)
        return ResponseEntity.ok(response)
    }
}
