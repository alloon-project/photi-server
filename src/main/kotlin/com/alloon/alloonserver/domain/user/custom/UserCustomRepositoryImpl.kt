package com.alloon.alloonserver.domain.user.custom

import com.alloon.alloonserver.domain.user.QContact.contact
import com.alloon.alloonserver.domain.user.QUser.user
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.service.user.dto.FindUserInfoDto
import com.alloon.alloonserver.service.user.dto.QFindUserInfoDto
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class UserCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : UserCustomRepository {

    override fun findFetchContact(email: String?, username: String?, userId: Long?): User? {
        return queryFactory
            .selectFrom(user)
            .innerJoin(user.contact, contact)
            .where(
                eqUserId(userId),
                eqUsername(username),
                eqEmail(email),
                contact.verifyYn.isTrue
            ).fetchOne()
    }

    override fun find(userId: Long): User? {
        return queryFactory
            .selectFrom(user)
            .where(user.id.eq(userId))
            .fetchOne()
    }

    override fun findInfoById(userId: Long): FindUserInfoDto? {
        return queryFactory
            .select(
                QFindUserInfoDto(
                    user.imageUrl,
                    user.username,
                    user.contact.email
                )
            )
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne()
    }

    private fun eqUserId(userId: Long?): BooleanExpression? = userId?.let { user.id.eq(it) }

    private fun eqEmail(email: String?): BooleanExpression? = email?.let { contact.email.eq(it) }

    private fun eqUsername(username: String?): BooleanExpression? =
        username?.let { user.username.eq(it) }
}