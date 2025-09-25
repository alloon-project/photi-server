package com.photi.utils

import java.security.Principal

class UserUtil {

    companion object {
        fun getUserId(principal: Principal): Long {
            return principal.name.toLong()
        }
    }
}
