package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.mission.Hashtag

interface HashtagCustomRepository {

    fun findAll(tags: List<String>): List<Hashtag>
}