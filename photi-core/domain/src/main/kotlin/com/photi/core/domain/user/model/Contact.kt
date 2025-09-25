package com.photi.core.domain.user.model

import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Contact(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id", nullable = false)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    val email: String,

    @Column(nullable = false, length = 6)
    var verificationCode: String,

    @Column(nullable = false)
    var verifyYn: Boolean = false,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @Column(nullable = true)
    var deletedDate: LocalDateTime? = null,
) : BaseEntity() {

    fun changeVerificationCode(verificationCode: String) {
        this.verificationCode = verificationCode
        this.verifyYn = false
    }

    fun verify(verificationCode: String) {
        if (this.verificationCode != verificationCode) {
            throw CustomException(ExceptionCode.EMAIL_VERIFICATION_CODE_INVALID)
        }
        this.verifyYn = true
    }

    fun softDelete() {
        isDeleted = true
        deletedDate = LocalDateTime.now()
    }

    fun updateReRegisterStatus() {
        isDeleted = false
        deletedDate = null
    }
}
