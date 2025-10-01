package com.photi.core.domain.user.adapter

import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.inquiry.port.InquiryUserPort
import com.photi.core.domain.report.port.ReportUserPort
import com.photi.core.domain.user.query.UserQueryService
import org.springframework.stereotype.Component

@Component
class UserAdapter(
    private val userQueryService: UserQueryService,
) : ReportUserPort, InquiryUserPort {

    override fun getUserBy(id: Long) {
        userQueryService.getUserBy(id)
            .orElseThrow { throw CustomException(ExceptionCode.USER_NOT_FOUND) }
    }
}
