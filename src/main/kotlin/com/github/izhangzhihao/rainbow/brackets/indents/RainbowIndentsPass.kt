package com.github.izhangzhihao.rainbow.brackets.indents

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.codeInsight.highlighting.CodeBlockSupportHandler
import com.intellij.ide.actions.ToggleZenModeAction
import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.IndentGuideDescriptor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Segment
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.TokenSet
import com.intellij.util.DocumentUtil
import com.intellij.util.containers.IntStack
import com.intellij.util.text.CharArrayUtil
import java.lang.StrictMath.abs
import java.lang.StrictMath.min
import java.util.*

/** From [com.intellij.codeInsight.daemon.impl.IndentsPass]
 * Commit history: https://sourcegraph.com/github.com/JetBrains/intellij-community/-/blob/platform/lang-impl/src/com/intellij/codeInsight/daemon/impl/IndentsPass.java#tab=history
 * mirror changes start from `Make it possible to ignore indent guides more granularly and do so for C#`
 * */
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

    private fun nowStamp(): Long = if (isRainbowIndentGuidesShown(this.myProject)) document.modificationStamp xor (EditorUtil.getTabSize(myEditor).toLong() shl 24) else -1

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
        DocumentUtil.executeInBulk(document, myRanges.size > 10000) {
            for (i in startRangeIndex until myRanges.size) {
                newHighlighters.add(createHighlighter(mm, myRanges[i]))
            }
        }

        myEditor.putUserData(INDENT_HIGHLIGHTERS_IN_EDITOR_KEY, newHighlighters)
        myEditor.indentsModel.assumeIndents(myDescriptors)
    }

    private fun buildDescriptors(): List<IndentGuideDescriptor> {
        if (!isRainbowIndentGuidesShown(this.myProject)) return emptyList()

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

    private inner class IndentsCalculator() {
        val myComments: MutableMap<Language, TokenSet> = HashMap()
        val lineIndents: IntArray // negative value means the line is empty (or contains a comment) and indent

        // (denoted by absolute value) was deduced from enclosing non-empty lines
        val myChars: CharSequence

        init {
            lineIndents = IntArray(document.lineCount)
            myChars = document.charsSequence
        }

        /**
         * Calculates line indents for the [target document][.myDocument].
         */
        fun calculate() {
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
                        val tokenType = iterator.tokenType
                        if (BraceMatchingUtil.isRBraceToken(iterator, myChars, fileType) ||
                                tokenType != null &&
                                CodeBlockSupportHandler.findMarkersRanges(myFile, tokenType.language, nonWhitespaceOffset).isNotEmpty()) {
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
            val tokenType = try {
                it.tokenType
            } catch (e: Throwable) {
                return false
            }
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

        private val RENDERER = RainbowIndentGuideRenderer()

        private fun isRainbowIndentGuidesShown(project: Project): Boolean {
            if (RainbowSettings.instance.disableRainbowIndentsInZenMode && isZenModeEnabled(project)) {
                return false
            }
            return RainbowSettings.instance.isRainbowEnabled && RainbowSettings.instance.isShowRainbowIndentGuides
        }

        private fun isZenModeEnabled(project: Project) =
                ToggleZenModeAction.isZenModeEnabled(project)

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
