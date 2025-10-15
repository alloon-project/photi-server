package com.photi.core.domain.report.model

enum class ReasonType(
    private val value: String,
) {
    REDUNDANT("중복/도배성"),
    OBSCENITY("음란성/선정적"),
    ABUSIVE("욕설/혐오"),
    DANGEROUS("폭력적/위험"),
    PROMOTION("상업적 홍보/광고"),
    SLANDER("타인 비방"),
    ETC("직접 작성"),
}
