package com.photi.server.domain.challenge

import com.photi.server.domain.challenge.custom.ChallengeTemplateImageCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface ChallengeTemplateImageRepository : JpaRepository<ChallengeTemplateImage, Long>,
    ChallengeTemplateImageCustomRepository {
}