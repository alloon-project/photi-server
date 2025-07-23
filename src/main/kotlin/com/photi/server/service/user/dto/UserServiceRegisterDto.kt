package com.photi.server.service.user.dto

import com.photi.server.domain.user.Contact
import com.photi.server.domain.user.Role
import com.photi.server.domain.user.User
import com.photi.server.domain.user.UserRole

data class UserServiceRegisterDto(
    val email: String,
    val username: String,
    var password: String,
) {

    fun toUserEntity(contact: Contact, userTemplateImage: String): User {
        return User(
            contact = contact,
            username = username,
            password = password,
            imageUrl = userTemplateImage
        )
    }

    fun toUserRoleEntity(user: User): UserRole {
        return UserRole(user = user, role = Role.USER)
    }
}
