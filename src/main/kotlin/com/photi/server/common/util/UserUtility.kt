package com.photi.server.common.util

import java.security.Principal

class UserUtility {

    companion object {
        fun getUserId(principal: Principal): Long {
            return principal.name.toLong()
        }
    }
}