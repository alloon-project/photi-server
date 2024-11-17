package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.common.constant.ExceptionCode
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.base.BaseEntity
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

    /**
     * 인증코드 변경
     * @param verificationCode 인증코드
     */
    fun changeVerificationCode(verificationCode: String) {
        this.verificationCode = verificationCode
        this.verifyYn = false
    }

    /**
     * 인증코드 검증
     * @param verificationCode 인증코드
     * @throws EMAIL_VERIFICATION_CODE_INVALID 400
     */
    fun verify(verificationCode: String) {
        if (this.verificationCode != verificationCode)
            throw CustomException(ExceptionCode.EMAIL_VERIFICATION_CODE_INVALID)
        this.verifyYn = true
    }

    fun softDelete() {
        isDeleted = true
        deletedDate = LocalDateTime.now()
    }
}