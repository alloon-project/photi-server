package com.alloon.alloonserver.domain.report.custom

import com.alloon.alloonserver.domain.base.ServiceStatus.ACTIVE
import com.alloon.alloonserver.domain.report.QReportCategory.reportCategory
import com.alloon.alloonserver.domain.report.ReportCategory
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ReportCategoryCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ReportCategoryCustomRepository {

    override fun findAllDescription(type: ReportCategoryType): List<String> {
        return queryFactory
            .select(reportCategory.description)
            .from(reportCategory)
            .where(
                reportCategory.type.eq(type),
                reportCategory.serviceStatus.eq(ACTIVE)
            ).orderBy(reportCategory.sort.asc())
            .fetch()
    }
}