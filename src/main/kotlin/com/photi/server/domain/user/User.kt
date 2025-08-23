package com.photi.server.domain.user

import com.photi.server.common.constant.ExceptionCode.CHALLENGE_LIMIT_EXCEED
import com.photi.server.common.response.CustomException
import com.photi.server.domain.base.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "users")
@Entity
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    val contact: Contact,

    @Column(nullable = false, length = 20, unique = true)
    val username: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false, length = 500)
    var imageUrl: String,

    @Column(nullable = false)
    var temporaryPasswordYn: Boolean = false,

    @Column(nullable = false)
    var feedCnt: Int = 0,

    @Column(nullable = false)
    var challengeCnt: Int = 0,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @Column(nullable = true)
    var deletedDate: LocalDateTime? = null,
) : BaseEntity() {

    fun resetPassword(password: String) {
        this.password = password
        this.temporaryPasswordYn = true
    }

    fun changePassword(password: String) {
        this.password = password
        this.temporaryPasswordYn = false
    }

    fun changeImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun updateFeedCnt() {
        feedCnt += 1
    }

    fun decreaseFeedCnt() {
        if (feedCnt > 0) {
            feedCnt -= 1
        }
    }

    fun updateChallengeCnt() {
        challengeCnt += 1
    }

    fun decreaseChallengeCnt() {
        if (challengeCnt > 0) {
            challengeCnt -= 1
        }
    }

    fun softDelete() {
        isDeleted = true
        deletedDate = LocalDateTime.now()
    }

    fun validateChallengeCnt() {
        if (challengeCnt >= CHALLENGE_LIMIT) {
            throw CustomException(CHALLENGE_LIMIT_EXCEED)
        }
    }

    companion object {
        private const val CHALLENGE_LIMIT = 20
    }
}
