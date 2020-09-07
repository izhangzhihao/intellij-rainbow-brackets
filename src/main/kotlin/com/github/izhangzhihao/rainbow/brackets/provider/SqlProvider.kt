package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.intellij.sql.psi.SqlTokens.SQL_BEGIN
import com.intellij.sql.psi.SqlTokens.SQL_END

class SqlProvider : PairedBraceProvider {
    override fun pairs(): List<BracePair> = listOf(BracePair(SQL_BEGIN, SQL_END, false))
}
