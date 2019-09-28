package com.github.izhangzhihao.rainbow.brackets.indents

import com.github.izhangzhihao.rainbow.brackets.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.util.alphaBlend
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.IndentGuideDescriptor
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Segment
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.DocumentUtil
import com.intellij.util.containers.IntStack
import com.intellij.util.text.CharArrayUtil
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.lang.StrictMath.*
import java.util.*

/** From [com.intellij.codeInsight.daemon.impl.IndentsPass] */
class RainbowIndentsPass internal constructor(
        project: Project,
        editor: Editor,
        private val myFile: PsiFile
) : TextEditorHighlightingPass(project, editor.document, false), DumbAware {

    private val myEditor: EditorEx = editor as EditorEx

    @Volatile
    private var myRanges = emptyList<TextRange>()

    @Volatile
    private var myDescriptors = emptyList<IndentGuideDescriptor>()

    override fun doCollectInformation(progress: ProgressIndicator) {
        val document = checkNotNull(myDocument)
        val stamp = myEditor.getUserData(LAST_TIME_INDENTS_BUILT)
        if (stamp != null && stamp.toLong() == nowStamp()) return

        myDescriptors = buildDescriptors()

        val ranges = ArrayList<TextRange>()
        for (descriptor in myDescriptors) {
            ProgressManager.checkCanceled()
            val endOffset = if (descriptor.endLine < document.lineCount) {
                document.getLineStartOffset(descriptor.endLine)
            } else {
                document.textLength
            }
            ranges.add(TextRange(document.getLineStartOffset(descriptor.startLine), endOffset))
        }

        Collections.sort(ranges, Segment.BY_START_OFFSET_THEN_END_OFFSET)
        myRanges = ranges
    }

    private fun nowStamp(): Long {
        val settings = RainbowSettings.instance
        return if (settings.isRainbowEnabled && settings.isShowRainbowIndentGuides) {
            checkNotNull(myDocument).modificationStamp
        } else -1
    }

    override fun doApplyInformationToEditor() {
        val stamp = myEditor.getUserData(LAST_TIME_INDENTS_BUILT)
        val nowStamp = nowStamp()

        if (stamp == nowStamp) return

        myEditor.putUserData(LAST_TIME_INDENTS_BUILT, nowStamp)

        val oldHighlighters = myEditor.getUserData(INDENT_HIGHLIGHTERS_IN_EDITOR_KEY)
        if (nowStamp == -1L) {
            if (oldHighlighters != null) {
                for (oldHighlighter in oldHighlighters) {
                    oldHighlighter.dispose()
                }
                oldHighlighters.clear()
            }
            return
        }

        val newHighlighters = ArrayList<RangeHighlighter>()
        val mm = myEditor.markupModel
        var curRange = 0

        if (oldHighlighters != null) {
            // after document change some range highlighters could have become invalid, or the order could have been broken
            oldHighlighters.sortWith(Comparator.comparing { h: RangeHighlighter -> !h.isValid }
                    .thenComparing(Segment.BY_START_OFFSET_THEN_END_OFFSET))

            var curHighlight = 0
            while (curRange < myRanges.size && curHighlight < oldHighlighters.size) {
                val range = myRanges[curRange]
                val highlighter = oldHighlighters[curHighlight]
                if (!highlighter.isValid) break

                val cmp = compare(range, highlighter)
                when {
                    cmp < 0 -> {
                        newHighlighters.add(createHighlighter(mm, range))
                        curRange++
                    }
                    cmp > 0 -> {
                        highlighter.dispose()
                        curHighlight++
                    }
                    else -> {
                        newHighlighters.add(highlighter)
                        curHighlight++
                        curRange++
                    }
                }
            }

            while (curHighlight < oldHighlighters.size) {
                val highlighter = oldHighlighters[curHighlight]
                if (!highlighter.isValid) break
                highlighter.dispose()
                curHighlight++
            }
        }

        val startRangeIndex = curRange
        val document = checkNotNull(myDocument)
        DocumentUtil.executeInBulk(document, myRanges.size > 10000) {
            for (i in startRangeIndex until myRanges.size) {
                newHighlighters.add(createHighlighter(mm, myRanges[i]))
            }
        }

        myEditor.putUserData(INDENT_HIGHLIGHTERS_IN_EDITOR_KEY, newHighlighters)
        myEditor.indentsModel.assumeIndents(myDescriptors)
    }

    private fun buildDescriptors(): List<IndentGuideDescriptor> {
        if (!myEditor.settings.isIndentGuidesShown) return emptyList()

        val calculator = IndentsCalculator()
        calculator.calculate()
        val lineIndents = calculator.lineIndents

        val lines = IntStack()
        val indents = IntStack()

        lines.push(0)
        indents.push(0)
        val descriptors = ArrayList<IndentGuideDescriptor>()
        for (line in 1 until lineIndents.size) {
            ProgressManager.checkCanceled()
            val curIndent = abs(lineIndents[line])

            while (!indents.empty() && curIndent <= indents.peek()) {
                ProgressManager.checkCanceled()
                val level = indents.pop()
                val startLine = lines.pop()
                if (level > 0) {
                    for (i in startLine until line) {
                        if (level != abs(lineIndents[i])) {
                            descriptors.add(createDescriptor(level, startLine, line, lineIndents))
                            break
                        }
                    }
                }
            }

            val prevLine = line - 1
            val prevIndent = abs(lineIndents[prevLine])

            if (curIndent - prevIndent > 1) {
                lines.push(prevLine)
                indents.push(prevIndent)
            }
        }

        val document = checkNotNull(myDocument)
        while (!indents.empty()) {
            ProgressManager.checkCanceled()
            val level = indents.pop()
            val startLine = lines.pop()
            if (level > 0) {
                descriptors.add(createDescriptor(level, startLine, document.lineCount, lineIndents))
            }
        }
        return descriptors
    }

    private fun createDescriptor(
            level: Int,
            startLine: Int,
            endLine: Int,
            lineIndents: IntArray
    ): IndentGuideDescriptor {
        var sLine = startLine
        while (sLine > 0 && lineIndents[sLine] < 0) sLine--
        // int codeConstructStartLine = findCodeConstructStartLine(startLine);
        return IndentGuideDescriptor(level, sLine, endLine)
    }

    /*
    private fun findCodeConstructStart(startLine: Int): Int? {
        val document = myEditor.document
        val text = document.immutableCharSequence
        val lineStartOffset = document.getLineStartOffset(startLine)
        val firstNonWsOffset = CharArrayUtil.shiftForward(text, lineStartOffset, " \t")
        val type = PsiUtilBase.getPsiFileAtOffset(myFile, firstNonWsOffset).fileType
        val language = PsiUtilCore.getLanguageAtOffset(myFile, firstNonWsOffset)
        val braceMatcher = BraceMatchingUtil.getBraceMatcher(type, language)
        val iterator = myEditor.highlighter.createIterator(firstNonWsOffset)
        return if (braceMatcher.isLBraceToken(iterator, text, type)) {
            braceMatcher.getCodeConstructStart(myFile, firstNonWsOffset)
        } else null
    }


    private fun findCodeConstructStartLine(startLine: Int): Int {
        val codeConstructStart = findCodeConstructStart(startLine)
        return if (codeConstructStart != null) myEditor.document.getLineNumber(codeConstructStart) else startLine
    }
    */

    private inner class IndentsCalculator internal constructor() {
        internal val myComments: MutableMap<Language, TokenSet> = HashMap()
        internal val lineIndents: IntArray // negative value means the line is empty (or contains a comment) and indent
        // (denoted by absolute value) was deduced from enclosing non-empty lines
        internal val myChars: CharSequence

        init {
            val document = checkNotNull(myDocument)
            lineIndents = IntArray(document.lineCount)
            myChars = document.charsSequence
        }

        /**
         * Calculates line indents for the [target document][.myDocument].
         */
        internal fun calculate() {
            val document = checkNotNull(myDocument)
            val fileType = myFile.fileType
            val tabSize = EditorUtil.getTabSize(myEditor)

            for (line in lineIndents.indices) {
                ProgressManager.checkCanceled()
                val lineStart = document.getLineStartOffset(line)
                val lineEnd = document.getLineEndOffset(line)
                var offset = lineStart
                var column = 0
                outer@ while (offset < lineEnd) {
                    when (myChars[offset]) {
                        ' ' -> column++
                        '\t' -> column = (column / tabSize + 1) * tabSize
                        else -> break@outer
                    }
                    offset++
                }
                // treating commented lines in the same way as empty lines
                // Blank line marker
                lineIndents[line] = if (offset == lineEnd || isComment(offset)) -1 else column
            }

            var topIndent = 0
            var line = 0
            while (line < lineIndents.size) {
                ProgressManager.checkCanceled()
                if (lineIndents[line] >= 0) {
                    topIndent = lineIndents[line]
                } else {
                    val startLine = line
                    while (line < lineIndents.size && lineIndents[line] < 0) {
                        line++
                    }

                    val bottomIndent = if (line < lineIndents.size) lineIndents[line] else topIndent

                    var indent = min(topIndent, bottomIndent)
                    if (bottomIndent < topIndent) {
                        val lineStart = document.getLineStartOffset(line)
                        val lineEnd = document.getLineEndOffset(line)
                        val nonWhitespaceOffset = CharArrayUtil.shiftForward(myChars, lineStart, lineEnd, " \t")
                        val iterator = myEditor.highlighter.createIterator(nonWhitespaceOffset)
                        if (BraceMatchingUtil.isRBraceToken(iterator, myChars, fileType)) {
                            indent = topIndent
                        }
                    }

                    for (blankLine in startLine until line) {
                        assert(lineIndents[blankLine] == -1)
                        lineIndents[blankLine] = -min(topIndent, indent)
                    }


                    line-- // will be incremented back at the end of the loop;
                }
                line++
            }
        }

        private fun isComment(offset: Int): Boolean {
            val it = myEditor.highlighter.createIterator(offset)
            val tokenType = it.tokenType
            val language = tokenType.language
            var comments: TokenSet? = myComments[language]
            if (comments == null) {
                val definition = LanguageParserDefinitions.INSTANCE.forLanguage(language)
                if (definition != null) {
                    comments = definition.commentTokens
                }
                if (comments == null) {
                    return false
                } else {
                    myComments[language] = comments
                }
            }
            return comments.contains(tokenType)
        }
    }

    companion object {
        private val INDENT_HIGHLIGHTERS_IN_EDITOR_KEY = Key.create<MutableList<RangeHighlighter>>("_INDENT_HIGHLIGHTERS_IN_EDITOR_KEY_")
        private val LAST_TIME_INDENTS_BUILT = Key.create<Long>("_LAST_TIME_INDENTS_BUILT_")

        private val XML_TAG_PARENT_CONDITION = Condition<PsiElement> { it is XmlTag }

        private val RENDERER: CustomHighlighterRenderer = CustomHighlighterRenderer renderer@{ editor, highlighter, g ->
            if (editor !is EditorEx) return@renderer

            val rainbowInfo = getRainbowInfo(editor, highlighter) ?: return@renderer

            val startOffset = highlighter.startOffset
            val doc = highlighter.document
            if (startOffset >= doc.textLength) return@renderer

            val endOffset = highlighter.endOffset
            val endLine = doc.getLineNumber(endOffset)

            var off: Int
            var startLine = doc.getLineNumber(startOffset)
            val descriptor = editor.indentsModel.getDescriptor(startLine, endLine)

            val chars = doc.charsSequence
            do {
                val start = doc.getLineStartOffset(startLine)
                val end = doc.getLineEndOffset(startLine)
                off = CharArrayUtil.shiftForward(chars, start, end, " \t")
                startLine--
            } while (startLine > 1 && off < doc.textLength && chars[off] == '\n')

            val startPosition = editor.offsetToVisualPosition(off)
            var indentColumn = startPosition.column

            // It's considered that indent guide can cross not only white space but comments, javadoc etc. Hence, there is a possible
            // case that the first indent guide line is, say, single-line comment where comment symbols ('//') are located at the first
            // visual column. We need to calculate correct indent guide column then.
            var lineShift = 1
            if (indentColumn <= 0 && descriptor != null) {
                indentColumn = descriptor.indentLevel
                lineShift = 0
            }
            if (indentColumn <= 0) return@renderer

            val foldingModel = editor.foldingModel
            if (foldingModel.isOffsetCollapsed(off)) return@renderer

            val headerRegion = foldingModel.getCollapsedRegionAtOffset(doc.getLineEndOffset(doc.getLineNumber(off)))
            val tailRegion = foldingModel.getCollapsedRegionAtOffset(doc.getLineStartOffset(doc.getLineNumber(endOffset)))

            if (tailRegion != null && tailRegion === headerRegion) return@renderer

            val guide = editor.indentsModel.caretIndentGuide
            val selected = if (guide != null) {
                val caretModel = editor.caretModel
                val caretOffset = caretModel.offset
                caretOffset in off until endOffset && caretModel.logicalPosition.column == indentColumn
            } else false

            val start = editor.visualPositionToXY(VisualPosition(startPosition.line + lineShift, indentColumn))
            val endPosition = editor.offsetToVisualPosition(endOffset)
            val end = editor.visualPositionToXY(VisualPosition(endPosition.line, endPosition.column))
            var maxY = end.y
            if (endPosition.line == editor.offsetToVisualPosition(doc.textLength).line) {
                maxY += editor.lineHeight
            }

            val clip = g.clipBounds
            if (clip != null) {
                if (clip.y >= maxY || clip.y + clip.height <= start.y) {
                    return@renderer
                }
                maxY = min(maxY, clip.y + clip.height)
            }

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
            if (selected) {
                g.drawLine(start.x + 2, start.y, start.x + 2, maxY - 1)
            } else {
                var y = start.y
                var newY = start.y
                val softWrapModel = editor.softWrapModel
                val lineHeight = editor.lineHeight
                var i = max(0, startLine + lineShift)
                while (i < endLine && newY < maxY) {
                    val softWraps = softWrapModel.getSoftWrapsForLine(i)
                    var logicalLineHeight = softWraps.size * lineHeight
                    if (i > startLine + lineShift) {
                        logicalLineHeight += lineHeight // We assume that initial 'y' value points just below the target line.
                    }
                    if (softWraps.isNotEmpty() && softWraps[0].indentInColumns < indentColumn) {
                        if (y < newY || i > startLine + lineShift) { // There is a possible case that soft wrap is located on indent start line.
                            g.drawLine(start.x + 2, y, start.x + 2, newY + lineHeight - 1)
                        }
                        newY += logicalLineHeight
                        y = newY
                    } else {
                        newY += logicalLineHeight
                    }

                    val foldRegion = foldingModel.getCollapsedRegionAtOffset(doc.getLineEndOffset(i))
                    if (foldRegion != null && foldRegion.endOffset < doc.textLength) {
                        i = doc.getLineNumber(foldRegion.endOffset)
                    }
                    i++
                }

                if (y < maxY) {
                    g.drawLine(start.x + 2, y, start.x + 2, maxY - 1)
                }
            }
        }

        private fun getRainbowInfo(editor: EditorEx, highlighter: RangeHighlighter): RainbowInfo? {
            val document = editor.document
            val project = editor.project ?: return null
            val psiFile = PsiManager.getInstance(project).findFile(editor.virtualFile) ?: return null
            var element = psiFile.findElementAt(highlighter.endOffset)?.parent ?: return null

            var rainbowInfo = RainbowInfo.RAINBOW_INFO_KEY[element]
            if (rainbowInfo == null && psiFile is XmlFile && element is XmlElement && element !is XmlTag) {
                element = PsiTreeUtil.findFirstParent(element, true, XML_TAG_PARENT_CONDITION) ?: return null
                rainbowInfo = RainbowInfo.RAINBOW_INFO_KEY[element] ?: return null
            }

            if (document.getLineNumber(element.startOffset) < document.getLineNumber(highlighter.startOffset)) {
                return null
            }

            return rainbowInfo
        }

        private fun createHighlighter(mm: MarkupModel, range: TextRange): RangeHighlighter {
            return mm.addRangeHighlighter(
                    range.startOffset,
                    range.endOffset,
                    0,
                    null,
                    HighlighterTargetArea.EXACT_RANGE
            ).apply {
                customRenderer = RENDERER
            }
        }

        private fun compare(r: TextRange, h: RangeHighlighter): Int {
            val answer = r.startOffset - h.startOffset
            return if (answer != 0) answer else r.endOffset - h.endOffset
        }
    }
}
