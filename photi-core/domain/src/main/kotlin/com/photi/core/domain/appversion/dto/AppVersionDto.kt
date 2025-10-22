package com.photi.core.domain.appversion.dto

import com.photi.core.domain.appversion.model.OsType

data class AppVersionDto(
    val os: OsType,
    val appVersion: String,
)
