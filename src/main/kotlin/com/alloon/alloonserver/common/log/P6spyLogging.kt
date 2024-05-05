package com.alloon.alloonserver.common.log

import com.p6spy.engine.logging.Category
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import org.hibernate.engine.jdbc.internal.FormatStyle

class P6spyLogging : MessageFormattingStrategy {
    override fun formatMessage(
        connectionId: Int,
        now: String?,
        elapsed: Long,
        category: String?,
        prepared: String?,
        sql: String?,
        url: String?,
    ): String {
        val uuid = InterceptorLogging().requestId
        var sqlLog = "[" + uuid + " | DATABASE] connectionId=" + connectionId + ", time=" + elapsed + "ms"

        if (sql == null || sql.trim() == "")
            return sqlLog

        if (Category.STATEMENT.name == category) {
            val tmpSql = sql.trim().lowercase()

            sqlLog += if (tmpSql.startsWith("create") ||
                tmpSql.startsWith("drop") ||
                tmpSql.startsWith("alter") ||
                tmpSql.startsWith("truncate"))
                FormatStyle.DDL.formatter.format(sql)
            else
                FormatStyle.BASIC.formatter.format(sql)
        }

        return sqlLog
    }
}