package com.github.izhangzhihao.rainbow.brackets.lite.util

import com.intellij.openapi.editor.Document

fun Document.lineNumber(offset: Int): Int? = if (offset in 0 until textLength) getLineNumber(offset) else null
