package com.alloon.alloonserver.domain.challenge.custom

import java.time.LocalDateTime

interface ChallengeTemplateImageCustomRepository {

    fun findAllImageUrl(now: LocalDateTime): MutableList<String>
}