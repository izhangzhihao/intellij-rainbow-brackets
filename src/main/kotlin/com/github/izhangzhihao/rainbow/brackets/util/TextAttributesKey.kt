package com.github.izhangzhihao.rainbow.brackets.util

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import kotlin.reflect.jvm.isAccessible

fun create(name: String, textAttributes: TextAttributes): TextAttributesKey {
    val cons = TextAttributesKey::class.constructors.first { it.parameters.size == 3 }
    cons.isAccessible = true
    return cons.call(name, textAttributes, null)
}