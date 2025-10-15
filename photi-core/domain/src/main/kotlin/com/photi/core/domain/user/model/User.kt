package com.photi.core.domain.user.model

import com.photi.core.domain.common.model.BaseEntity
import com.photi.core.domain.user.dto.ChangePasswordDto
import com.photi.core.domain.user.dto.SignUpRequestDto
import com.photi.core.domain.user.port.PasswordPort
import com.photi.core.domain.common.consts.DirectoryType
import com.photi.core.domain.user.port.UserS3Port
import com.photi.core.domain.user.validator.UserValidator
import com.photi.utils.CodeUtil.getAuthenticationCode
import com.photi.utils.PasswordUtil.getTemporaryPassword
import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "users")
@Entity
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    val email: String,

    @Column(nullable = false, length = 6)
    var authenticationCode: String,

    @Column(nullable = false)
    var isAuthenticated: Boolean = false,

    @Column(nullable = true, unique = true, length = 20)
    val username: String? = null,

    @Column(nullable = true)
    var password: String? = null,

    @Column(nullable = true, length = 500)
    var imageUrl: String? = null,

    @Column(nullable = false)
    var isTemporaryPassword: Boolean = false,

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 15)
    var role: RoleType = RoleType.UNAUTHENTICATED_USER,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @Column(nullable = true)
    var deletedDate: LocalDateTime? = null,
) : BaseEntity() {

    fun issueNewAuthenticationCode(userValidator: UserValidator, email: String) {
        userValidator.validateEmail(this, email)
        notAuthenticated()
    }

    fun authenticated(userValidator: UserValidator, authenticationCode: String) {
        userValidator.validateAuthenticationCode(this, authenticationCode)
        isAuthenticated = true
    }

    fun signUp(
        userValidator: UserValidator,
        passwordPort: PasswordPort,
        dto: SignUpRequestDto,
    ) {
        userValidator.validateNewUser(dto.email, dto.username)
        password = passwordPort.encode(dto.password)
        role = RoleType.USER
    }

    fun resetPasswordTo(passwordPort: PasswordPort) {
        password = passwordPort.encode(getTemporaryPassword())
        isTemporaryPassword = true
    }

    fun changePasswordTo(
        userValidator: UserValidator,
        passwordPort: PasswordPort,
        dto: ChangePasswordDto,
    ) {
        userValidator.validateNewPassword(this, dto)
        password = passwordPort.encode(dto.newPassword)
        isTemporaryPassword = false
    }

    fun withdraw(passwordPort: PasswordPort, password: String) {
        passwordPort.validateMatches(password, this.password!!)
        isDeleted = true
        deletedDate = LocalDateTime.now()
    }

    fun changeImageUrl(s3Port: UserS3Port, imageUrl: String) {
        if (!isImageNullOrEmpty()) {
            s3Port.deleteImage(this.imageUrl!!, DirectoryType.USERS)
        }
        this.imageUrl = imageUrl
    }

    fun changeReSignUpStatus() {
        isDeleted = false
        deletedDate = null
    }

    private fun notAuthenticated() {
        authenticationCode = getAuthenticationCode()
        isAuthenticated = false
    }

    private fun isImageNullOrEmpty() = this.imageUrl.isNullOrEmpty()
}
