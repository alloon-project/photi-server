package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.service.user.UserService
import com.alloon.alloonserver.common.constant.SuccessCode.FOUND_MY_USER_INFO
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.config.auth.JwtProvider
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Validated
@RestController
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/api/v1/users")
    fun getMyInfo(principal: Principal): ResponseEntity<DefaultSingleResponse> {
        val response = userService.getInfo(UserUtility.getUserId(principal))

        return DefaultSingleResponse.toResponseEntity(FOUND_MY_USER_INFO, response)
    }
}