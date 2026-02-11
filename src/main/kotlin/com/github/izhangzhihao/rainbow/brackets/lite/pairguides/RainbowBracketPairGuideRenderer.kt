package com.github.izhangzhihao.rainbow.brackets.lite.pairguides

import com.github.izhangzhihao.rainbow.brackets.lite.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.lite.util.alphaBlend
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.impl.view.EditorPainter
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.Key
import com.intellij.ui.paint.LinePainter2D
import com.intellij.util.Processor
import java.awt.Graphics
import java.awt.Graphics2D

class RainbowBracketPairGuideRenderer(
    internal val rainbowInfo: RainbowInfo
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

        val isActivePair = getActivePair(editor) === highlighter
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

    companion object {
        private val LAST_ACTIVE_CARET_OFFSET = Key.create<Int>("_RB_LAST_ACTIVE_CARET_OFFSET_")
        private val ACTIVE_PAIR_HIGHLIGHTER_KEY = Key.create<RangeHighlighter>("_RB_ACTIVE_PAIR_HIGHLIGHTER_KEY_")

        internal fun invalidateActivePairCache(editor: EditorEx) {
            editor.putUserData(LAST_ACTIVE_CARET_OFFSET, null)
            editor.putUserData(ACTIVE_PAIR_HIGHLIGHTER_KEY, null)
        }

        internal fun peekActivePair(editor: EditorEx): RangeHighlighter? {
            return editor.getUserData(ACTIVE_PAIR_HIGHLIGHTER_KEY)
        }

        internal fun getActivePair(editor: EditorEx): RangeHighlighter? {
            val caretOffset = editor.caretModel.offset
            val cachedCaretOffset = editor.getUserData(LAST_ACTIVE_CARET_OFFSET)
            val cachedHighlighter = editor.getUserData(ACTIVE_PAIR_HIGHLIGHTER_KEY)
            if (cachedCaretOffset == caretOffset) {
                if (cachedHighlighter?.isValid == true) {
                    return cachedHighlighter
                }
            }

            var activeHighlighter: RangeHighlighter? = null
            val doc = editor.document
            val textLength = doc.textLength
            if (textLength > 0) {
                val queryStart = caretOffset.coerceIn(0, textLength - 1)
                val queryEnd = (queryStart + 1).coerceAtMost(textLength)
                val markupModel = editor.markupModel as? MarkupModelEx
                if (markupModel != null) {
                    markupModel.processRangeHighlightersOverlappingWith(queryStart, queryEnd, Processor { item ->
                        if (!item.isValid) return@Processor true
                        if (item.customRenderer !is RainbowBracketPairGuideRenderer) return@Processor true
                        if (caretOffset !in item.startOffset until item.endOffset) return@Processor true

                        val endBracketOffset = (item.endOffset - 1).coerceAtLeast(item.startOffset)
                        val startLine = doc.getLineNumber(item.startOffset)
                        val endLine = doc.getLineNumber(endBracketOffset)
                        if (endLine <= startLine) return@Processor true

                        activeHighlighter = chooseBetterActive(activeHighlighter, item)
                        true
                    })
                } else {
                    val highlighters = editor.getUserData(RainbowBracketPairGuidesPass.BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY)
                    if (highlighters != null) {
                        for (item in highlighters) {
                            if (!item.isValid) continue
                            if (caretOffset !in item.startOffset until item.endOffset) continue

                            val endBracketOffset = (item.endOffset - 1).coerceAtLeast(item.startOffset)
                            val startLine = doc.getLineNumber(item.startOffset)
                            val endLine = doc.getLineNumber(endBracketOffset)
                            if (endLine <= startLine) continue

                            activeHighlighter = chooseBetterActive(activeHighlighter, item)
                        }
                    }
                }
            }

            editor.putUserData(LAST_ACTIVE_CARET_OFFSET, caretOffset)
            editor.putUserData(ACTIVE_PAIR_HIGHLIGHTER_KEY, activeHighlighter)
            return activeHighlighter
        }

        private fun chooseBetterActive(current: RangeHighlighter?, candidate: RangeHighlighter): RangeHighlighter {
            if (current == null) return candidate
            val candidateLength = candidate.endOffset - candidate.startOffset
            val currentLength = current.endOffset - current.startOffset
            return if (candidateLength < currentLength ||
                (candidateLength == currentLength && candidate.startOffset > current.startOffset)
            ) {
                candidate
            } else {
                current
            }
        }
    }
}
