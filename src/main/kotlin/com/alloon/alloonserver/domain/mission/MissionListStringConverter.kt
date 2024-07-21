package com.alloon.alloonserver.domain.mission

import jakarta.persistence.AttributeConverter

class MissionListStringConverter : AttributeConverter<List<String>, String> {

    private val SPLIT_CHAR = ";"

    override fun convertToDatabaseColumn(attribute: List<String>?): String? {
        return attribute?.joinToString(SPLIT_CHAR)
    }

    override fun convertToEntityAttribute(dbData: String?): List<String>? {
        return dbData?.split(SPLIT_CHAR)
    }
}