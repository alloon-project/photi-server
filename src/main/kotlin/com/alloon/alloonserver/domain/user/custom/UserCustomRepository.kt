package com.alloon.alloonserver.domain.user.custom

interface UserCustomRepository {

    fun findFetchContact(email: String): com.alloon.alloonserver.domain.user.User?
}