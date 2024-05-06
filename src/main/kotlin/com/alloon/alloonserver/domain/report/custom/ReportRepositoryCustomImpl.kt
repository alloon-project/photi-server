package com.alloon.alloonserver.domain.report.custom

import com.alloon.alloonserver.domain.report.QReport.report
import com.alloon.alloonserver.domain.report.Report
import com.alloon.alloonserver.domain.user.QUser.user
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ReportRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : ReportRepositoryCustom {

    override fun find(reporterId: Long): Report? {
        return queryFactory
            .selectFrom(report)
            .innerJoin(report.reporter, user)
            .where(
                report.reporter.id.eq(reporterId)
            ).fetchFirst()
    }


}