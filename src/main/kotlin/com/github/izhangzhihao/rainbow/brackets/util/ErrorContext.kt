package com.github.izhangzhihao.rainbow.brackets.util

import com.intellij.openapi.diagnostic.Attachment
import com.intellij.util.ExceptionUtil

data class ErrorContext(val stackTrace: String) {
    var message: String? = null
    var errorClass: String = ""
    var description: String = ""
    var pluginName: String = ""
    var pluginVersion: String = "Unknown"
    var attachments: List<Attachment> = emptyList()

    companion object {
        fun fromThrowable(throwable: Throwable): ErrorContext {
            val throwableText = ExceptionUtil.getThrowableText(throwable)
            val errorContext = ErrorContext(throwableText)
            errorContext.message = throwable.message
            val firstLine = throwableText.lines()[0]
            errorContext.errorClass = firstLine.substringBeforeLast(':')
            return errorContext
        }
    }
}