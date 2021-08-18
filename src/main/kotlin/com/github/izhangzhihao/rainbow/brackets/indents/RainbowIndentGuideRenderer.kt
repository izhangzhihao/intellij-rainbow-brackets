package com.github.izhangzhihao.rainbow.brackets.indents

import com.github.izhangzhihao.rainbow.brackets.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.util.*
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SoftWrap
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.impl.view.EditorPainter
import com.intellij.openapi.editor.impl.view.VisualLinesIterator
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType
import com.intellij.ui.paint.LinePainter2D
import com.intellij.util.text.CharArrayUtil
import java.awt.Graphics
import java.awt.Graphics2D

/** From [com.intellij.codeInsight.daemon.impl.IndentGuideRenderer]
 *  Commit history : https://sourcegraph.com/github.com/JetBrains/intellij-community/-/blob/platform/lang-impl/src/com/intellij/codeInsight/daemon/impl/IndentGuideRenderer.java#tab=history
 * */
class RainbowIndentGuideRenderer: CustomHighlighterRenderer {
    override fun paint(editor: Editor, highlighter: RangeHighlighter, g: Graphics) {
        if (editor !is EditorEx) return

        val rainbowInfo = getRainbowInfo(editor, highlighter) ?: return

        val startOffset = highlighter.startOffset
        val doc = highlighter.document
        if (startOffset >= doc.textLength) return

        val endOffset = highlighter.endOffset

        var off: Int
        var startLine = doc.getLineNumber(startOffset)

        val chars = doc.charsSequence
        do {
            val start = doc.getLineStartOffset(startLine)
            val end = doc.getLineEndOffset(startLine)
            off = CharArrayUtil.shiftForward(chars, start, end, " \t")
            startLine--
        } while (startLine > 1 && off < doc.textLength && chars[off] == '\n')

        val startPosition = editor.offsetToVisualPosition(off)
        val indentColumn = startPosition.column

        if (indentColumn <= 0) return

        val foldingModel = editor.foldingModel
        if (foldingModel.isOffsetCollapsed(off)) return

        val headerRegion = foldingModel.getCollapsedRegionAtOffset(doc.getLineEndOffset(doc.getLineNumber(off)))
        val tailRegion = foldingModel.getCollapsedRegionAtOffset(doc.getLineStartOffset(doc.getLineNumber(endOffset)))

        if (tailRegion != null && tailRegion === headerRegion) return

        val guide = editor.indentsModel.caretIndentGuide
        val selected = if (guide != null) {
            val caretModel = editor.caretModel
            val caretOffset = caretModel.offset
            caretOffset in off until endOffset && caretModel.logicalPosition.column == indentColumn
        } else false

        val lineHeight = editor.getLineHeight()
        val start = editor.visualPositionToXY(startPosition)
        start.y += lineHeight
        val endPosition = editor.offsetToVisualPosition(endOffset)
        val end = editor.visualPositionToXY(endPosition)
        var maxY = end.y
        if (endPosition.line == editor.offsetToVisualPosition(doc.textLength).line) {
            maxY += lineHeight
        }

        val clip = g.clipBounds
        if (clip != null) {
            if (clip.y >= maxY || clip.y + clip.height <= start.y) {
                return
            }
            maxY = StrictMath.min(maxY, clip.y + clip.height)
        }
        if (start.y >= maxY) return
        val targetX = Math.max(0, start.x + EditorPainter.getIndentGuideShift(editor)).toDouble()
        g.color = if (selected) {
            rainbowInfo.color
        } else {
            val defaultBackground = editor.colorsScheme.defaultBackground
            rainbowInfo.color.alphaBlend(defaultBackground, 0.2f)
        }

        // There is a possible case that indent line intersects soft wrap-introduced text. Example:
        //     this is a long line <soft-wrap>
        // that| is soft-wrapped
        //     |
        //     | <- vertical indent
        //
        // Also it's possible that no additional intersections are added because of soft wrap:
        //     this is a long line <soft-wrap>
        //     |   that is soft-wrapped
        //     |
        //     | <- vertical indent
        // We want to use the following approach then:
        //     1. Show only active indent if it crosses soft wrap-introduced text;
        //     2. Show indent as is if it doesn't intersect with soft wrap-introduced text;
        val softWraps = editor.softWrapModel.registeredSoftWraps
        if (selected || softWraps.isEmpty()) {
            LinePainter2D.paint(g as Graphics2D, targetX, start.y.toDouble(), targetX, maxY - 1.toDouble())
        } else {
            var startY = start.y
            var startVisualLine = startPosition.line + 1
            if (clip != null && startY < clip.y) {
                startY = clip.y
                startVisualLine = editor.yToVisualLine(clip.y)
            }
            val it = VisualLinesIterator(editor as EditorImpl, startVisualLine)
            while (!it.atEnd()) {
                val currY: Int = it.y
                if (currY >= startY) {
                    if (currY >= maxY) break
                    if (it.startsWithSoftWrap()) {
                        val softWrap: SoftWrap = softWraps[it.startOrPrevWrapIndex]
                        if (softWrap.indentInColumns < indentColumn) {
                            if (startY < currY) {
                                LinePainter2D.paint((g as Graphics2D), targetX, startY.toDouble(), targetX, currY - 1.toDouble())
                            }
                            startY = currY + lineHeight
                        }
                    }
                }
                it.advance()
            }
            if (startY < maxY) {
                LinePainter2D.paint((g as Graphics2D), targetX, startY.toDouble(), targetX, maxY - 1.toDouble())
            }
        }
    }

    companion object {
        private val XML_TAG_PARENT_CONDITION = Condition<PsiElement> { it is XmlTag }
        private val XML_END_TAG_START_CONDITION: (PsiElement) -> Boolean = { element ->
            element is XmlToken && element.tokenType == XmlTokenType.XML_END_TAG_START
        }
        private val XML_TAG_END_CONDITION: (PsiElement) -> Boolean = { element ->
            element is XmlToken && element.tokenType == XmlTokenType.XML_TAG_END
        }

        private fun getRainbowInfo(editor: EditorEx, highlighter: RangeHighlighter): RainbowInfo? {
            val virtualFile = editor.virtualFile?.takeIf { it.isValid } ?: return null
            val document = editor.document
            val project = editor.project ?: return null
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return null
            var element = try {
                psiFile.findElementAt(highlighter.endOffset)?.parent ?: return null
            } catch (e: Throwable) {
                return null
            }

            var rainbowInfo = RainbowInfo.RAINBOW_INFO_KEY[element]
            if (rainbowInfo == null && psiFile is XmlFile && element !is XmlTag) {
                element = PsiTreeUtil.findFirstParent(element, true, XML_TAG_PARENT_CONDITION) ?: return null
                rainbowInfo = RainbowInfo.RAINBOW_INFO_KEY[element] ?: return null
            }

            if (!element.isValid || !checkBoundary(document, element, highlighter)) {
                return null
            }

            return rainbowInfo
        }

        /***
         * introduced from https://github.com/izhangzhihao/intellij-rainbow-brackets/commit/d9d40e6910e9c15fbdcba12280df18019ea170b5
         */
        private fun checkBoundary(document: Document, element: PsiElement, highlighter: RangeHighlighter): Boolean {
            val elementStartLine = document.lineNumber(element.startOffset) ?: return false
            val highlighterStartLine = document.lineNumber(highlighter.startOffset) ?: return false

            var xmlStartTagEndLine: Int? = null
            var xmlEndTagStartLine: Int? = null

            val isValidStartBoundary = if (element is XmlTag) {
                /*
                 *     <tag                  // [*] element & highlighter start line
                 *     | <- vertical indent
                 *     >                     // [*] highlighter start/end line, start tag end line
                 *     | <- vertical indent
                 *     </tag                 // [*] highlighter start/end line, end tag start line
                 *     | <- vertical indent
                 *     >                     // [ ] element/highlighter end line
                 */
                xmlStartTagEndLine = element.getStartTagEndLineNumber(document)
                xmlEndTagStartLine = element.getEndTagStartLineNumber(document)

                highlighterStartLine == elementStartLine ||
                        highlighterStartLine == xmlStartTagEndLine ||
                        highlighterStartLine == xmlEndTagStartLine
            } else {
                /*
                 * Element start line > Highlighter start line:
                 *     function foo(arg1,   // highlighter start line
                 *     |            arg2) { // element start line
                 *     | <- vertical indent
                 *     }                    // element & highlighter end line
                 */
                elementStartLine >= highlighterStartLine
            }
            if (!isValidStartBoundary) {
                return false
            }

            val elementEndLine = document.lineNumber(element.endOffset) ?: return false
            val highlighterEndLine = document.lineNumber(highlighter.endOffset) ?: return false
            val isValidEndBoundary = if (element is XmlTag) {
                /*
                 *     <tag                  // [ ] element & highlighter start line
                 *     | <- vertical indent
                 *     >                     // [*] highlighter start/end line, start tag end line
                 *     | <- vertical indent
                 *     </tag                 // [*] highlighter start/end line, end tag start line
                 *     | <- vertical indent
                 *     >                     // [*] element/highlighter end line
                 */
                highlighterEndLine == elementEndLine ||
                        highlighterEndLine == xmlStartTagEndLine ||
                        highlighterEndLine == xmlEndTagStartLine
            } else {
                /*
                 * Element end line != Highlighter end line:
                 *     function foo() {     // element & highlighter start line
                 *     | <- vertical indent
                 *   var bar = "bar";       // highlighter end line
                 *     }                    // element end line
                 */
                elementEndLine == highlighterEndLine
            }
            if (!isValidEndBoundary) {
                return false
            }

            return true
        }

        private fun XmlTag.getStartTagEndLineNumber(document: Document): Int? =
            firstChild?.findNextSibling(XML_TAG_END_CONDITION)?.let { document.lineNumber(it.startOffset) }

        private fun XmlTag.getEndTagStartLineNumber(document: Document): Int? =
            lastChild?.findPrevSibling(XML_END_TAG_START_CONDITION)?.let { document.lineNumber(it.startOffset) }

    }
}