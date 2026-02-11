package com.github.izhangzhihao.rainbow.brackets.lite.pairguides

import com.github.izhangzhihao.rainbow.brackets.lite.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.lite.util.alphaBlend
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.view.EditorPainter
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.ui.paint.LinePainter2D
import java.awt.Graphics
import java.awt.Graphics2D

class RainbowBracketPairGuideRenderer(
    private val rainbowInfo: RainbowInfo
) : CustomHighlighterRenderer {
    override fun paint(editor: Editor, highlighter: RangeHighlighter, g: Graphics) {
        if (editor !is EditorEx || g !is Graphics2D) return

        val doc = highlighter.document
        val startOffset = highlighter.startOffset
        val endOffset = highlighter.endOffset
        if (startOffset < 0 || endOffset <= startOffset || endOffset > doc.textLength) return

        val endBracketOffset = (endOffset - 1).coerceAtLeast(startOffset)
        if (editor.foldingModel.isOffsetCollapsed(startOffset) || editor.foldingModel.isOffsetCollapsed(endBracketOffset)) return

        val startPosition = editor.offsetToVisualPosition(startOffset)
        val endPosition = editor.offsetToVisualPosition(endBracketOffset)
        if (endPosition.line <= startPosition.line) return

        val lineHeight = editor.lineHeight
        val startXY = editor.visualPositionToXY(startPosition)
        val endXY = editor.visualPositionToXY(endPosition)

        val x = (startXY.x + EditorPainter.getIndentGuideShift(editor)).coerceAtLeast(0).toDouble()
        val y1 = (startXY.y + lineHeight).toDouble()
        val y2 = (endXY.y + lineHeight / 2.0)
        if (y1 >= y2) return

        val clip = g.clipBounds ?: return
        if (clip.y >= y2 || clip.y + clip.height <= y1) return

        val currentRange = TextRange(startOffset, endOffset)
        val activeRange = getActivePairRange(editor)
        val isActivePair = activeRange == currentRange
        g.color = if (isActivePair) {
            rainbowInfo.color
        } else {
            rainbowInfo.color.alphaBlend(editor.colorsScheme.defaultBackground, 0.2f)
        }

        LinePainter2D.paint(g, x, y1, x, y2)

        val minTailWidth = 6.0
        val tailEndX = endXY.x.toDouble().coerceAtLeast(x + minTailWidth)
        LinePainter2D.paint(g, x, y2, tailEndX, y2)
    }

    private fun getActivePairRange(editor: EditorEx): TextRange? {
        val caretOffset = editor.caretModel.offset
        val cachedCaretOffset = editor.getUserData(LAST_ACTIVE_CARET_OFFSET)
        if (cachedCaretOffset == caretOffset) {
            return editor.getUserData(ACTIVE_PAIR_RANGE_KEY)
        }

        val highlighters = editor.getUserData(RainbowBracketPairGuidesPass.BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY)
        var activeRange: TextRange? = null
        val doc = editor.document
        if (highlighters != null) {
            for (item in highlighters) {
                if (!item.isValid) continue
                if (caretOffset !in item.startOffset..item.endOffset) continue

                val candidate = TextRange(item.startOffset, item.endOffset)
                val endBracketOffset = (candidate.endOffset - 1).coerceAtLeast(candidate.startOffset)
                val startLine = doc.getLineNumber(candidate.startOffset)
                val endLine = doc.getLineNumber(endBracketOffset)
                // VSCode-like guides highlight the active multi-line bracket block,
                // not single-line expression pairs.
                if (endLine <= startLine) continue

                activeRange = if (activeRange == null || candidate.length < activeRange.length ||
                    (candidate.length == activeRange.length && candidate.startOffset > activeRange.startOffset)
                ) {
                    candidate
                } else {
                    activeRange
                }
            }
        }

        editor.putUserData(LAST_ACTIVE_CARET_OFFSET, caretOffset)
        editor.putUserData(ACTIVE_PAIR_RANGE_KEY, activeRange)
        return activeRange
    }

    companion object {
        private val LAST_ACTIVE_CARET_OFFSET = Key.create<Int>("_RB_LAST_ACTIVE_CARET_OFFSET_")
        private val ACTIVE_PAIR_RANGE_KEY = Key.create<TextRange>("_RB_ACTIVE_PAIR_RANGE_KEY_")

        internal fun invalidateActivePairCache(editor: EditorEx) {
            editor.putUserData(LAST_ACTIVE_CARET_OFFSET, null)
            editor.putUserData(ACTIVE_PAIR_RANGE_KEY, null)
        }
    }
}
