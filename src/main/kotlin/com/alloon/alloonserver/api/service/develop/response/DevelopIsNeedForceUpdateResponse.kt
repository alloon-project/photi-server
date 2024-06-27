package com.alloon.alloonserver.api.service.develop.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "앱 강제 업데이트 필요 여부 응답 객체")
data class DevelopIsNeedForceUpdateResponse(
    @Schema(description = "앱 강제 업데이트 필요 여부", example = "true")
    val updateYn: Boolean,
)