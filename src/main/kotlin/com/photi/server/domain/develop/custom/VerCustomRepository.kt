package com.photi.server.domain.develop.custom

interface VerCustomRepository {

    fun exists(version: String): Boolean
}