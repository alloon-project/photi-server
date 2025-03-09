package com.photi.server.domain.user.custom

import com.photi.server.domain.user.QUser.user
import com.photi.server.domain.user.QUserRole.userRole
import com.photi.server.domain.user.UserRole
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class UserRoleCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserRoleCustomRepository {

    override fun findAllFetchUser(userId: Long): List<UserRole> {
        return queryFactory
            .select(userRole)
            .from(userRole)
            .innerJoin(userRole.user, user)
            .fetchJoin()
            .where(user.id.eq(userId))
            .fetch()
    }
}