package com.photi.core.infra

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(PhotiConfigImportSelector::class)
annotation class EnablePhotiConfig(
    val value: Array<PhotiConfigGroup>,
)
