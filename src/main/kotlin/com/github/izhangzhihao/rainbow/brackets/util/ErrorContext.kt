package com.github.izhangzhihao.rainbow.brackets.util

import com.intellij.openapi.diagnostic.Attachment
import com.intellij.util.ExceptionUtil

data class ErrorContext(val stackTrace: String) {
    var message: String? = null
    var description: String = ""
    var pluginName: String = ""
    var pluginVersion: String = "Unknown"
    var attachments: List<Attachment> = emptyList()

    companion object {
        fun fromThrowable(throwable: Throwable): ErrorContext {
            val errorContext = ErrorContext(ExceptionUtil.getThrowableText(throwable))
            errorContext.message = throwable.message
            return errorContext
        }
    }
}