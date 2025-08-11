package com.photi.server.common.util

class AppVersionUtility {

    companion object {

        private const val DELIMITER = "."
        private const val ZERO = 0

        fun compareVersions(version1: String, version2: String): Int {
            val list1 = version1.split(DELIMITER)
            val list2 = version2.split(DELIMITER)

            for (i in ZERO until maxOf(list1.size, list2.size)) {
                val num1 = list1.getOrNull(i)?.toIntOrNull() ?: ZERO
                val num2 = list2.getOrNull(i)?.toIntOrNull() ?: ZERO
                return num1.compareTo(num2)
            }
            return ZERO
        }
    }
}
