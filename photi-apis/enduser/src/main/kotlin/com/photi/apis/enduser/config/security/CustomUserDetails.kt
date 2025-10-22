package com.photi.apis.enduser.config.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val userId: String,
    private val role: String,
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf<SimpleGrantedAuthority>().apply {
            add(SimpleGrantedAuthority("$ROLE_PREFIX${role}"))
        }
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return userId
    }

    companion object {
        private const val ROLE_PREFIX = "ROLE_"
    }
}

fun CustomUserDetails.getUserId() = this.username.toLong()
