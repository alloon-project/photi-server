package com.photi.core.domain.challenge.adapter

import com.photi.core.domain.challenge.validator.ChallengeMemberValidator
import com.photi.core.domain.report.port.ChallengeMemberPort
import org.springframework.stereotype.Component

@Component("CHALLENGE_MEMBER")
class ChallengeMemberAdapter(
    private val challengeMemberValidator: ChallengeMemberValidator,
) : ChallengeMemberPort {

    override fun validateExistsBy(targetId: Long) {
        challengeMemberValidator.validateExistsBy(targetId)
    }
}
