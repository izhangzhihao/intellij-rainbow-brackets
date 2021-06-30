package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.intellij.sql.psi.SqlCommonKeywords.SQL_CASE
import com.intellij.sql.psi.SqlTokens.SQL_BEGIN
import com.intellij.sql.psi.SqlTokens.SQL_END

class SqlBracePairProvider : BracePairProvider {
    override fun pairs(): List<BracePair> = listOf(
        BracePair(SQL_BEGIN, SQL_END, false),
        BracePair(SQL_CASE, SQL_END, false),
    )
}
