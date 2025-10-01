package com.photi.apis.enduser.controller.inquiry.dto.request

import com.photi.core.domain.inquiry.dto.CreateInquiryDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "문의하기 요청 객체")
data class CreateInquiryRequest(

    @Schema(
        description = "문의 내용 타입",
        examples = ["SERVICE_USE", "SUGGESTION", "ERROR", "ETC"],
    )
    @field:NotBlank(message = "문의 내용 타입은 필수 입력입니다.")
    @field:Pattern(
        regexp = "SERVICE_USE|SUGGESTION|ERROR|ETC",
        message = "문의 내용 타입은 SERVICE_USE(서비스 이용 문의), SUGGESTION(개선/제안 요청), ERROR(오류 문의), ETC(기타 문의) 중 하나여야 합니다.",
    )
    val type: String,

    @Schema(description = "문의 내용", example = "서비스 이용 관련 문의입니다.")
    @field:NotBlank(message = "문의 내용은 필수 입력입니다.")
    @field:Size(min = 1, max = 120, message = "문의 내용은 1~120자만 가능합니다.")
    val content: String,
) {

    fun toServiceDto() = CreateInquiryDto(type, content)
}