package com.github.izhangzhihao.rainbow.brackets.action

import com.github.izhangzhihao.rainbow.brackets.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
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
                    attributes,
                    false, //hideByTextChange
                    RainbowSettings.instance.pressAnyKeyToRemoveTheHighlightingEffects, //hideByAnyKey
                    highlighters)
        }

        val endOffset = rainbowInfo.endOffset
        val lastOffset = document.textLength
        if (endOffset < lastOffset) {
            highlightManager.addRangeHighlight(this,
                    endOffset,
                    lastOffset,
                    attributes,
                    false, //hideByTextChange
                    RainbowSettings.instance.pressAnyKeyToRemoveTheHighlightingEffects, //hideByAnyKey
                    highlighters)
        }

        return highlighters
    }

}