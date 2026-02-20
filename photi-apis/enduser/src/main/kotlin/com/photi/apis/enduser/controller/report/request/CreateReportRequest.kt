package com.photi.apis.enduser.controller.report.request

import com.photi.core.domain.report.dto.CreateReportDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "신고 등록 요청 객체")
data class CreateReportRequest(

    @Schema(
        description = "신고 카테고리",
        examples = ["CHALLENGE", "CHALLENGE_MEMBER", "FEED"]
    )
    @field:NotBlank(message = "신고 카테고리는 필수 입력입니다.")
    @field:Pattern(
        regexp = "CHALLENGE|CHALLENGE_MEMBER|FEED",
        message = "신고 카테고리는 CHALLENGE(챌린지 신고), CHALLENGE_MEMBER(챌린지 파티원 신고), FEED(피드 신고) 중 하나여야 됩니다."
    )
    val category: String,

    @Schema(
        description = "신고 이유",
        examples = ["REDUNDANT", "OBSCENITY", "ABUSIVE", "DANGEROUS", "PROMOTION", "SLANDER", "ETC"]
    )
    @field:NotBlank(message = "신고 이유는 필수 입력입니다.")
    @field:Pattern(
        regexp = "REDUNDANT|OBSCENITY|ABUSIVE|DANGEROUS|PROMOTION|SLANDER|ETC",
        message = "신고 이유는 REDUNDANT(중복/도배성), OBSCENITY(음란성/선정적), ABUSIVE(욕설/혐오), DANGEROUS(폭력적/위험), PROMOTION(상업적 홍보/광고), SLANDER(타인 비방), ETC(직접 작성) 중 하나여야 됩니다."
    )
    val reason: String,

    @Schema(description = "신고 내용", example = "신고 내용입니다.")
    @field:Size(max = 120, message = "신고 내용은 0~120자만 가능합니다.")
    val content: String?,
) {

    fun toServiceDto() = CreateReportDto(category, reason, content)
}
