package com.github.izhangzhihao.rainbow.brackets.lite.action

import com.github.izhangzhihao.rainbow.brackets.lite.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.lite.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.lite.util.alphaBlend
import com.github.izhangzhihao.rainbow.brackets.lite.util.create
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Font
import java.util.*

class ScopeOutsideHighlightingRestrainAction : AbstractScopeHighlightingAction() {

    override fun Editor.addHighlighter(highlightManager: HighlightManager,
                                       rainbowInfo: RainbowInfo): Collection<RangeHighlighter> {
        val defaultBackground = EditorColorsManager.getInstance().globalScheme.defaultBackground
        val background = Color.GRAY.alphaBlend(defaultBackground, 0.05f)
        val foreground = Color.GRAY.alphaBlend(defaultBackground, 0.55f)
        val attributes = TextAttributes(foreground, background, background, EffectType.BOXED, Font.PLAIN)
        val highlighters = LinkedList<RangeHighlighter>()

        val startOffset = rainbowInfo.startOffset
        if (startOffset > 0) {
            highlightManager.addRangeHighlight(this,
                    0,
                    startOffset,
                    attributes, //create("ScopeOutsideHighlightingRestrainAction", attributes),
                    false, //hideByTextChange
                    false, //hideByAnyKey
                    highlighters)
        }

        val endOffset = rainbowInfo.endOffset
        val lastOffset = document.textLength
        if (endOffset < lastOffset) {
            highlightManager.addRangeHighlight(this,
                    endOffset,
                    lastOffset,
                    attributes, //create("ScopeOutsideHighlightingRestrainAction", attributes),
                    false, //hideByTextChange
                    false, //hideByAnyKey
                    highlighters)
        }

        return highlighters
    }

}