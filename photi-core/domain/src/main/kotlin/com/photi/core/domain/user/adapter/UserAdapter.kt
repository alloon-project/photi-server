package com.photi.core.domain.user.adapter

import com.photi.core.domain.inquiry.port.InquiryUserPort
import com.photi.core.domain.report.port.ReportUserPort
import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.user.service.query.UserQueryService
import org.springframework.stereotype.Component

@Component
class UserAdapter(
    private val userQueryService: UserQueryService,
) : ReportUserPort, InquiryUserPort {

    override fun getUserBy(id: Long) {
        userQueryService.getUserBy(id).orElseThrow {
            throw UserException.NotFoundUserException()
        }
    }
}
