package com.photi.server.api.controller.inquiry

import com.photi.server.api.controller.inquiry.request.CreateInquiryRequest
import com.photi.server.common.constant.ExceptionCode.*
import com.photi.server.common.response.ApiErrorResponses
import com.photi.server.common.response.StringSuccessResponse
import com.photi.server.common.util.UserUtility
import com.photi.server.config.SwaggerConfig.Companion.ACCESS_TOKEN_KEY
import com.photi.server.service.inquiry.InquiryService
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
import java.security.Principal

@Validated
@RestController
@RequestMapping("/api/inquiries")
@Tag(name = "Inquiry", description = "문의 API")
class InquiryController(
    private val inquiryService: InquiryService,
) {

    @PostMapping
    @Operation(summary = "문의하기", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, USER_NOT_FOUND])
    fun createChallenge(
        principal: Principal,
        @RequestBody @Valid request: CreateInquiryRequest,
    ): ResponseEntity<StringSuccessResponse> {
        inquiryService.createInquiry(UserUtility.getUserId(principal), request.toServiceDto())

        return ResponseEntity.status(CREATED).body(StringSuccessResponse("문의 접수가 완료되었습니다."))
    }
}