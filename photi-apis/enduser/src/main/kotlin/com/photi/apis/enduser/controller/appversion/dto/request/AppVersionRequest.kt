package com.photi.apis.enduser.controller.appversion.dto.request

import com.photi.core.domain.appversion.dto.AppVersionDto
import com.photi.core.domain.appversion.model.OsType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "앱 버전 요청 객체")
data class AppVersionRequest(

    @Schema(description = "OS", example = "ANDROID")
    @field:NotBlank(message = "OS는 필수 입력입니다.")
    @field:Pattern(
        regexp = "ANDROID|IOS|android|ios",
        message = "OS는 ANDROID(android), IOS(ios) 중 하나여야 됩니다.",
    )
    val os: String,

    @Schema(description = "앱 버전", example = "1.0.0")
    @field:NotBlank(message = "앱 버전은 필수 입력입니다.")
    val appVersion: String,
) {

    fun toServiceDto() = AppVersionDto(OsType.valueOf(os.uppercase()), appVersion)
}
