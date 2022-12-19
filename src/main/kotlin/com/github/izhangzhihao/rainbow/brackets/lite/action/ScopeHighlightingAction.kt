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
import java.awt.Font
import java.util.*


class ScopeHighlightingAction : AbstractScopeHighlightingAction() {

    override fun Editor.addHighlighter(highlightManager: HighlightManager,
                                       rainbowInfo: RainbowInfo): Collection<RangeHighlighter> {
        val defaultBackground = EditorColorsManager.getInstance().globalScheme.defaultBackground
        val background = rainbowInfo.color.alphaBlend(defaultBackground, 0.2f)
        val attributes = TextAttributes(null, background, rainbowInfo.color, EffectType.BOXED, Font.PLAIN)
        val highlighters = LinkedList<RangeHighlighter>()
        highlightManager.addRangeHighlight(this,
                rainbowInfo.startOffset,
                rainbowInfo.endOffset,
                attributes, //create("ScopeHighlightingAction", attributes),
                false, //hideByTextChange
                false, //hideByAnyKey
                highlighters)

        return highlighters
    }

}