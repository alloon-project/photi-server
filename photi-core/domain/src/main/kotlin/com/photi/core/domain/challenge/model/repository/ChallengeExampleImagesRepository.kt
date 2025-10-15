package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.model.ChallengeExampleImages
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChallengeExampleImagesRepository : JpaRepository<ChallengeExampleImages, Long> {

    @Query("select c.imageUrl from ChallengeExampleImages c order by c.id")
    fun findExampleImages(): List<String>
}
