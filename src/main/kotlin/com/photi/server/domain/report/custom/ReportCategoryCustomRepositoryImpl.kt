package com.photi.server.domain.report.custom

import com.photi.server.domain.base.ServiceStatus
import com.photi.server.domain.base.ServiceStatus.ACTIVE
import com.photi.server.domain.report.QReportCategory.reportCategory
import com.photi.server.domain.report.ReportCategory
import com.photi.server.domain.report.ReportCategoryType
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ReportCategoryCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ReportCategoryCustomRepository {

    override fun findAllDescription(type: ReportCategoryType?): List<String> {
        return queryFactory
            .select(reportCategory.description)
            .from(reportCategory)
            .where(
                eqType(type),
                eqServiceStatus(ACTIVE)
            ).orderBy(reportCategory.sort.asc())
            .fetch()
    }

    override fun find(id: Long, type: ReportCategoryType?): ReportCategory? {
        return queryFactory
            .selectFrom(reportCategory)
            .where(
                reportCategory.id.eq(id),
                eqType(type),
                eqServiceStatus(ACTIVE)
            ).fetchFirst()
    }

    private fun eqType(type: ReportCategoryType?): BooleanExpression? =
        type?.let { reportCategory.type.eq(type) }

    private fun eqServiceStatus(serviceStatus: ServiceStatus?): BooleanExpression? =
        serviceStatus?.let { reportCategory.serviceStatus.eq(serviceStatus) }
}