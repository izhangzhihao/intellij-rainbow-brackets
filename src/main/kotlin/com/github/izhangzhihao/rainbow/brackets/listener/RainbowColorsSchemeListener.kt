package com.github.izhangzhihao.rainbow.brackets.listener

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsScheme

class RainbowColorsSchemeListener : EditorColorsListener {

    override fun globalSchemeChange(scheme: EditorColorsScheme?) {
        scheme?.let { RainbowHighlighter.fixHighlighting(it) }
    }
}