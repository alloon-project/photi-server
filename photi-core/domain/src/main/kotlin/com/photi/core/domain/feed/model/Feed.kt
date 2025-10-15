package com.photi.core.domain.feed.model

import com.photi.core.domain.common.consts.DirectoryType
import com.photi.core.domain.common.model.BasePermanentEntity
import com.photi.core.domain.feed.port.FeedS3Port
import jakarta.persistence.*

@Entity
@Table(
    indexes = [
        Index(columnList = "challenge_id, created_date_time"),
    ]
)
class Feed(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id", nullable = false)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val challengeMemberId: Long,

    @Column(nullable = false)
    val challengeId: Long,

    @Column(nullable = false, length = 500)
    var imageUrl: String,
) : BasePermanentEntity() {

    fun deleteImage(s3Port: FeedS3Port) {
        s3Port.deleteImage(imageUrl, DirectoryType.FEEDS)
    }
}
