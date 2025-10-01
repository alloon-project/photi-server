package com.photi.core.domain.challenge.adapter

import com.photi.core.domain.challenge.validator.ChallengeValidator
import com.photi.core.domain.report.port.ChallengePort
import org.springframework.stereotype.Component

@Component("CHALLENGE")
class ChallengeAdapter(
    private val challengeValidator: ChallengeValidator,
) : ChallengePort {

    override fun validateExistsBy(targetId: Long) {
        challengeValidator.validateExistsBy(targetId)
    }
}
