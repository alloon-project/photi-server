package com.alloon.alloonserver.domain.user.custom

import com.alloon.alloonserver.domain.user.QUser.user
import com.alloon.alloonserver.domain.user.QUserRole.userRole
import com.alloon.alloonserver.domain.user.UserRole
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