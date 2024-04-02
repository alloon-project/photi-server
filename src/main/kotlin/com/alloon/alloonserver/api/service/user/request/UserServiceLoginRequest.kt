package com.alloon.alloonserver.api.service.user.request

data class UserServiceLoginRequest(
    var username: String,
    var password: String,
)