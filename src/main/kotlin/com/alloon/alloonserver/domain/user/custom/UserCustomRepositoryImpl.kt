package com.alloon.alloonserver.domain.user.custom

import com.alloon.alloonserver.domain.base.ServiceStatus.END
import com.alloon.alloonserver.domain.challenge.QChallengeMember.challengeMember
import com.alloon.alloonserver.domain.user.QContact.contact
import com.alloon.alloonserver.domain.user.QUser.user
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.service.user.dto.QUserChallengeHistoryDto
import com.alloon.alloonserver.service.user.dto.QUserInfoDto
import com.alloon.alloonserver.service.user.dto.UserChallengeHistoryDto
import com.alloon.alloonserver.service.user.dto.UserInfoDto
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
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

    override fun findInfoById(userId: Long): UserInfoDto? {
        return queryFactory
            .select(
                QUserInfoDto(
                    user.imageUrl,
                    user.username,
                    user.contact.email
                )
            )
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne()
    }

    override fun findChallengeHistoryById(userId: Long): UserChallengeHistoryDto? {
        val endedChallengeCnt = queryFactory
            .select(challengeMember.count())
            .from(challengeMember)
            .join(challengeMember.user)
            .join(challengeMember.challenge)
            .where(
                challengeMember.user.id.eq(userId)
                    .and(challengeMember.challenge.serviceStatus.eq(END))
            )
            .fetchOne()?.toInt() ?: 0

        return queryFactory
            .select(
                QUserChallengeHistoryDto(
                    user.username,
                    user.imageUrl,
                    user.feedCnt,
                    Expressions.constant(endedChallengeCnt),
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