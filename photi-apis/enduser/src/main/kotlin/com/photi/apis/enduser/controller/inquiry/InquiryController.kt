package com.photi.apis.enduser.controller.inquiry

import com.photi.apis.enduser.common.exception.GlobalApiErrorResponses
import com.photi.apis.enduser.common.exception.UserApiErrorResponses
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.config.security.AuthUser
import com.photi.apis.enduser.config.security.CustomUserDetails
import com.photi.apis.enduser.config.security.getUserId
import com.photi.apis.enduser.controller.inquiry.dto.request.CreateInquiryRequest
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.*
import com.photi.core.domain.inquiry.service.InquiryService
import com.photi.core.domain.user.exception.UserErrorCode.USER_NOT_FOUND
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v2/inquiries")
@Tag(name = "Inquiry", description = "문의 API")
class InquiryController(
    private val inquiryService: InquiryService,
) {

    @PostMapping
    @Operation(summary = "문의하기", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun createChallenge(
        @AuthUser user: CustomUserDetails,
        @RequestBody @Valid request: CreateInquiryRequest,
    ): ResponseEntity<StringSuccessResponse> {
        inquiryService.createInquiry(user.getUserId(), request.toServiceDto())
        return ResponseEntity.status(CREATED)
            .body(StringSuccessResponse("문의 접수가 완료되었습니다."))
    }
}
