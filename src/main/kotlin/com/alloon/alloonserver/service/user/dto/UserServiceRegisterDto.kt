package com.alloon.alloonserver.service.user.dto

import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.Role
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRole

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
