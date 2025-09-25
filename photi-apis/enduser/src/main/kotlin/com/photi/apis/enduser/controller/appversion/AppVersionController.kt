package com.photi.apis.enduser.controller.appversion

import com.photi.apis.enduser.common.exception.ApiErrorResponses
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.config.SwaggerConfig.Companion.ACCESS_TOKEN_KEY
import com.photi.apis.enduser.controller.appversion.dto.request.AppVersionRequest
import com.photi.apis.enduser.controller.appversion.dto.response.AppVersionResponse
import com.photi.core.domain.appversion.usecase.AppVersionService
import com.photi.core.domain.common.exception.ExceptionCode
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

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

    @PatchMapping
    @Operation(summary = "앱 최소 버전 업데이트", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED])
    fun updateAppVersion(@RequestBody @Valid request: AppVersionRequest): ResponseEntity<StringSuccessResponse> {
        appVersionService.updateAppVersion(request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("앱 최소 버전 업데이트가 완료되었습니다."))
    }
}
