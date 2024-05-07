package com.alloon.alloonserver.domain.feed.custom

import com.alloon.alloonserver.domain.feed.Feed

interface FeedCustomRepository {

    fun find(id: Long): Feed?
}