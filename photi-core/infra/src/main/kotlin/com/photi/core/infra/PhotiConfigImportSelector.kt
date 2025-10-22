package com.photi.core.infra

import org.springframework.context.annotation.DeferredImportSelector
import org.springframework.core.type.AnnotationMetadata

class PhotiConfigImportSelector : DeferredImportSelector {

    override fun selectImports(metadata: AnnotationMetadata): Array<String> {
        return getValues(metadata)
            .map { it.configClass.name }
            .toTypedArray()
    }

    private fun getValues(metadata: AnnotationMetadata): Array<PhotiConfigGroup> {
        val attributes = metadata.getAnnotationAttributes(EnablePhotiConfig::class.java.name)
        val values = attributes?.get("value") as? Array<*>
        return values?.filterIsInstance<PhotiConfigGroup>()?.toTypedArray() ?: emptyArray()
    }
}
