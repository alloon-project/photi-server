package com.photi.core.domain.user.model

import com.photi.core.domain.common.consts.DirectoryType
import com.photi.core.domain.common.model.BaseTimeEntity
import com.photi.core.domain.user.dto.ChangePasswordDto
import com.photi.core.domain.user.dto.SignUpRequestDto
import com.photi.core.domain.user.port.PasswordPort
import com.photi.core.domain.user.port.UserS3Port
import com.photi.core.domain.user.validator.UserValidator
import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "users")
@Entity
class User(
    email: String,
    authenticationCode: String,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null
        protected set

    @Column(nullable = false, unique = true, length = 100)
    var email: String = email
        protected set

    @Column(nullable = false, length = 6)
    var authenticationCode: String = authenticationCode
        protected set

    @Column(nullable = false)
    var isAuthenticated: Boolean = false
        protected set

    @Column(nullable = true, unique = true, length = 20)
    var username: String? = null
        protected set

    @Column(nullable = true, length = 255)
    var password: String? = null
        protected set

    @Column(nullable = true, length = 500)
    var imageUrl: String? = null
        protected set

    @Column(nullable = false)
    var isTemporaryPassword: Boolean = false
        protected set

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 25)
    var role: RoleType = RoleType.UNAUTHENTICATED_USER
        protected set

    @Column(nullable = true)
    var deletedDate: LocalDateTime? = null
        protected set

    fun issueNewAuthenticationCode(
        userValidator: UserValidator,
        email: String,
        authenticationCode: String,
    ) {
        userValidator.validateEmail(this, email)
        this.authenticationCode = authenticationCode
        isAuthenticated = false
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
        username = dto.username
        password = passwordPort.encode(dto.password)
        role = RoleType.USER
    }

    fun resetPasswordTo(passwordPort: PasswordPort, temporaryPassword: String) {
        password = passwordPort.encode(temporaryPassword)
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
        role = RoleType.DELETED_USER
        deletedDate = LocalDateTime.now()
    }

    fun changeImageUrl(s3Port: UserS3Port, imageUrl: String) {
        if (!isImageNullOrEmpty()) {
            s3Port.deleteImage(this.imageUrl!!, DirectoryType.USERS)
        }
        this.imageUrl = imageUrl
    }

    fun changeReSignUpStatus() {
        role = RoleType.UNAUTHENTICATED_USER
        isAuthenticated = false
        deletedDate = null
    }

    private fun isImageNullOrEmpty() = this.imageUrl.isNullOrEmpty()
}
