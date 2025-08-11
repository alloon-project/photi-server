package com.photi.server.api.controller.appversion.request

import com.photi.server.domain.appversion.OsType
import com.photi.server.service.appversion.dto.AppVersionDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "앱 강제 업데이트 필요 여부 조회 요청 객체")
data class AppVersionRequest(

    @Schema(description = "OS", example = "ANDROID")
    @field:NotBlank(message = "OS는 필수 입력입니다.")
    @field:Pattern(
        regexp = "ANDROID|IOS",
        message = "OS는 ANDROID, IOS 중 하나여야 됩니다.",
    )
    val os: String,

    @Schema(description = "앱 버전", example = "1.0.0")
    @field:NotBlank(message = "앱 버전은 필수 입력입니다.")
    val appVersion: String,
) {

    fun toServiceDto() = AppVersionDto(OsType.valueOf(os), appVersion)
}
