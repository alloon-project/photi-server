package com.alloon.alloonserver.domain.challenge

import com.alloon.alloonserver.domain.challenge.custom.ChallengeTemplateImageCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeTemplateImageRepository : JpaRepository<ChallengeTemplateImage, Long>,
    ChallengeTemplateImageCustomRepository {
}