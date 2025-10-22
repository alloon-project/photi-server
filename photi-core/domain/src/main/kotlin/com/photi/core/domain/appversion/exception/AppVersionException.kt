package com.photi.core.domain.appversion.exception

import com.photi.core.domain.common.exception.PhotiException

sealed class AppVersionException(errorCode: AppVersionErrorCode) : PhotiException(errorCode) {

    class NotFoundOsPolicyException : AppVersionException(AppVersionErrorCode.OS_POLICY_NOT_FOUND)
}
