package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.domain.base.BaseEntity
import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.user.User
import jakarta.persistence.*

@Entity
class Report(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_category_id", nullable = false)
    val reportCategory: ReportCategory,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    val reporter: User?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_member_id")
    val challengeMember: ChallengeMember? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    val challenge: Challenge? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed? = null,

    @Column(length = 120)
    val reason: String? = null,
) : BaseEntity() {
}