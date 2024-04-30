package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.service.user.UserService
import com.alloon.alloonserver.common.constant.SuccessCode
import com.alloon.alloonserver.common.constant.SuccessCode.FOUND_MY_USER_INFO
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.config.auth.JwtProvider
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
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

    @PostMapping("/api/v1/users/image", consumes = [MULTIPART_FORM_DATA_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun uploadImage(principal: Principal, @RequestPart(required = false) file: MultipartFile?):
            ResponseEntity<DefaultSingleResponse> {
        val response = userService.uploadImage(UserUtility.getUserId(principal), file)

        return DefaultSingleResponse.toResponseEntity(SuccessCode.USER_IMAGE_UPLOADED, response)
    }
}