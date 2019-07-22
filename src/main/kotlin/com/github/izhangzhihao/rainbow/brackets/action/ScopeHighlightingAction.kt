package com.github.izhangzhihao.rainbow.brackets.action

import com.github.izhangzhihao.rainbow.brackets.RainbowInfo
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Font
import java.util.*

/**
 * ScopeHighlightingAction
 *
 * Created by Yii.Guxing on 2018/05/15
 */
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
                attributes,
                false, //hideByTextChange
                false, //hideByAnyKey
                highlighters)

        return highlighters
    }

}