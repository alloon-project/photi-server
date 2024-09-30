package com.alloon.alloonserver.common.scheduler

import com.alloon.alloonserver.domain.challenge.ChallengeRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledTasks(
    private val challengeRepository: ChallengeRepository
) {

    @Scheduled(cron = "0 0 0 * * ?")
    fun updateEndedChallenges() {
        challengeRepository.bulkServiceStatusEnd()
    }
}