package com.photi.server.service.appversion.dto

import com.photi.server.domain.appversion.OsType

data class AppVersionDto(
    val os: OsType,
    val appVersion: String,
)
