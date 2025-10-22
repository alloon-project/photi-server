package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.model.ChallengeExampleImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChallengeExampleImagesRepository : JpaRepository<ChallengeExampleImage, Long> {

    @Query("select c.imageUrl from ChallengeExampleImage c order by c.id")
    fun findExampleImages(): List<String>
}
