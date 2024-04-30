package com.alloon.alloonserver.domain.develop.custom

interface VerCustomRepository {

    fun exists(version: String): Boolean
}