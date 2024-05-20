package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.mission.Hashtag
import com.alloon.alloonserver.domain.mission.QHashtag.hashtag
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class HashtagCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : HashtagCustomRepository{

    override fun findAll(tags: List<String>): List<Hashtag> {
        return queryFactory
            .selectFrom(hashtag)
            .where(hashtag.tag.`in`(tags))
            .fetch()
    }
}