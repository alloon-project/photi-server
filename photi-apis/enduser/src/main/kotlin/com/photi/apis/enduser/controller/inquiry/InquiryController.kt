package com.photi.apis.enduser.controller.inquiry

import com.photi.apis.enduser.common.exception.ApiErrorResponses
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.controller.inquiry.dto.request.CreateInquiryRequest
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.inquiry.usecase.InquiryService
import com.photi.apis.enduser.config.SwaggerConfig.Companion.ACCESS_TOKEN_KEY
import com.photi.utils.UserUtil
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
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.USER_NOT_FOUND])
    fun createChallenge(
        principal: Principal,
        @RequestBody @Valid request: CreateInquiryRequest,
    ): ResponseEntity<StringSuccessResponse> {
        inquiryService.createInquiry(UserUtil.getUserId(principal), request.toServiceDto())
        return ResponseEntity.status(CREATED)
            .body(StringSuccessResponse("문의 접수가 완료되었습니다."))
    }
}
