package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.intellij.sql.psi.SqlTokens.SQL_BEGIN
import com.intellij.sql.psi.SqlTokens.SQL_END

class SqlProvider : PairedBraceProvider {
    override val pairs: List<BracePair> = listOf(BracePair(SQL_BEGIN, SQL_END, false))
    override val blackList: List<BracePair> = emptyList()
}
