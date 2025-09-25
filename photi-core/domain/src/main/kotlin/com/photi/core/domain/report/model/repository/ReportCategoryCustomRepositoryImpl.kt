package com.photi.core.domain.report.model.repository

import com.photi.core.domain.common.model.ServiceStatus
import com.photi.core.domain.common.model.ServiceStatus.ACTIVE
import com.photi.core.domain.report.model.QReportCategory.reportCategory
import com.photi.core.domain.report.model.ReportCategory
import com.photi.core.domain.report.model.ReportCategoryType
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
