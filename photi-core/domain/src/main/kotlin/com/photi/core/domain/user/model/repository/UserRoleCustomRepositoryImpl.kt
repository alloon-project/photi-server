package com.photi.core.domain.user.model.repository

import com.photi.core.domain.user.model.QUser.user
import com.photi.core.domain.user.model.QUserRole.userRole
import com.photi.core.domain.user.model.UserRole
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
