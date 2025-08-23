package com.photi.server.domain.develop.custom

import com.photi.server.domain.develop.QVer.ver
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class VerCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : VerCustomRepository {

    override fun exists(version: String): Boolean {
        return queryFactory
            .select(ver.id)
            .from(ver)
            .where(ver.version.gt(version))
            .fetchFirst() != null
    }
}