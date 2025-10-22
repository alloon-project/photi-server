package com.photi.apis.enduser.controller.appversion.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "앱 강제 업데이트 필요 여부 조회 응답 객체")
data class AppVersionResponse(

    @Schema(description = "앱 강제 업데이트 필요 여부", example = "true")
    val forceUpdate: Boolean,
) {

    companion object {

        fun of(forceUpdate: Boolean) = AppVersionResponse(forceUpdate)
    }
}
