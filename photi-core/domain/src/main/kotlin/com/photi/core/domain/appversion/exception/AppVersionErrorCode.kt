package com.photi.core.domain.appversion.exception

import com.photi.core.domain.common.consts.HttpErrorStatus.NOT_FOUND
import com.photi.core.domain.common.exception.BaseErrorCode

enum class AppVersionErrorCode(
    override val status: Int,
    override val code: String,
    override val message: String,
    override val description: String? = null,
) : BaseErrorCode {
    OS_POLICY_NOT_FOUND(NOT_FOUND, "OS_POLICY_NOT_FOUND", "존재하지 않는 OS 정책입니다.");
}
