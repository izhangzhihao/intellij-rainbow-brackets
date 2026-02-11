package com.github.izhangzhihao.rainbow.brackets.lite.pairguides

import com.github.izhangzhihao.rainbow.brackets.lite.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.lite.util.alphaBlend
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.view.EditorPainter
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
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

        val caretOffset = editor.caretModel.offset
        val isActivePair = caretOffset in startOffset..endOffset
        g.color = if (isActivePair) {
            rainbowInfo.color
        } else {
            rainbowInfo.color.alphaBlend(editor.colorsScheme.defaultBackground, 0.35f)
        }

        LinePainter2D.paint(g, x, y1, x, y2)

        val minTailWidth = 6.0
        val tailEndX = endXY.x.toDouble().coerceAtLeast(x + minTailWidth)
        LinePainter2D.paint(g, x, y2, tailEndX, y2)
    }
}
