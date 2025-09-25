package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.Contact
import com.photi.core.domain.user.model.Role
import com.photi.core.domain.user.model.User
import com.photi.core.domain.user.model.UserRole

data class UserServiceRegisterDto(
    val email: String,
    val username: String,
    var password: String,
) {

    fun toUserEntity(contact: Contact, userTemplateImage: String) = User(
        contact = contact,
        username = username,
        password = password,
        imageUrl = userTemplateImage
    )

    fun toUserRoleEntity(user: User) = UserRole(user = user, role = Role.USER)
}
