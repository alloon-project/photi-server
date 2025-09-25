package com.photi.core.domain.feed.model

import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.challenge.model.ChallengeMember
import com.photi.core.domain.common.model.BasePermanentEntity
import jakarta.persistence.*

@Entity
class Feed(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_member_id", nullable = false)
    val challengeMember: ChallengeMember,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    val challenge: Challenge,

    @Column(nullable = false, length = 500)
    val imageUrl: String,

    @Column(nullable = false)
    var likeCnt: Int = 0,

    @Column(nullable = false)
    var commentCnt: Int = 0,
) : BasePermanentEntity() {

    fun updateCommentCnt() {
        commentCnt += 1
    }

    fun decreaseCommentCnt() {
        if (commentCnt > 0) {
            commentCnt -= 1
        }
    }

    fun updateLikeCnt() {
        likeCnt += 1
    }

    fun decreaseLikeCnt() {
        if (likeCnt > 0) {
            likeCnt -= 1
        }
    }
}
