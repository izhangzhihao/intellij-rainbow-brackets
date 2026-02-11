package com.github.izhangzhihao.rainbow.brackets.lite.pairguides

import com.github.izhangzhihao.rainbow.brackets.lite.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.lite.settings.RainbowSettings
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Segment
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.util.DocumentUtil
import java.util.ArrayList
import java.util.Comparator
import javax.swing.Timer

class RainbowBracketPairGuidesPass internal constructor(
    project: Project,
    editor: Editor,
    private val myFile: PsiFile
) : TextEditorHighlightingPass(project, editor.document, false), DumbAware {

    private val myEditor: EditorEx = editor as EditorEx

    @Volatile
    private var myRanges = emptyList<TextRange>()
    private val myRainbowInfoByRange = mutableMapOf<TextRange, RainbowInfo>()

    override fun doCollectInformation(progress: ProgressIndicator) {
        ensureDocumentStructureListenerInstalled()
        val stamp = myEditor.getUserData(LAST_TIME_GUIDES_BUILT)
        if (stamp != null && stamp.toLong() == nowStamp()) return

        if (!isBracketPairGuidesShown()) {
            myRanges = emptyList()
            myRainbowInfoByRange.clear()
            return
        }

        val uniqueRanges = LinkedHashMap<TextRange, RainbowInfo>()
        myFile.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                ProgressManager.checkCanceled()
                val rainbowInfo = element.getUserData(RainbowInfo.RAINBOW_INFO_KEY)
                if (rainbowInfo != null) {
                    val startOffset = rainbowInfo.startOffset
                    val endOffset = rainbowInfo.endOffset
                    if (startOffset >= 0 && endOffset > startOffset && endOffset <= document.textLength) {
                        // Keep single-line pairs as well so that when user inserts a newline,
                        // shifted highlighters can become visible immediately without waiting
                        // for a full pair-guides rebuild.
                        val range = TextRange(startOffset, endOffset)
                        uniqueRanges.putIfAbsent(range, rainbowInfo)
                    }
                }
                super.visitElement(element)
            }
        })

        val sortedRanges = uniqueRanges.keys.sortedWith(Segment.BY_START_OFFSET_THEN_END_OFFSET)
        myRanges = sortedRanges
        myRainbowInfoByRange.clear()
        for (range in sortedRanges) {
            myRainbowInfoByRange[range] = uniqueRanges.getValue(range)
        }
    }

    override fun doApplyInformationToEditor() {
        ensureDocumentStructureListenerInstalled()
        ensureCaretRepaintListenerInstalled()

        val nowStamp = nowStamp()
        val oldStamp = myEditor.getUserData(LAST_TIME_GUIDES_BUILT)
        if (oldStamp == nowStamp) return
        myEditor.putUserData(LAST_TIME_GUIDES_BUILT, nowStamp)

        RainbowBracketPairGuideRenderer.invalidateActivePairCache(myEditor)

        val oldHighlighters = myEditor.getUserData(BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY)
        if (nowStamp == -1L) {
            if (oldHighlighters != null) {
                for (highlighter in oldHighlighters) {
                    highlighter.dispose()
                }
                oldHighlighters.clear()
            }
            return
        }

        val newHighlighters = ArrayList<RangeHighlighter>(myRanges.size)
        val mm = myEditor.markupModel
        var curRange = 0
        var dirtyStartOffset = Int.MAX_VALUE
        var dirtyEndOffset = -1

        fun markDirty(startOffset: Int, endOffset: Int) {
            dirtyStartOffset = minOf(dirtyStartOffset, startOffset)
            dirtyEndOffset = maxOf(dirtyEndOffset, endOffset)
        }

        if (oldHighlighters != null) {
            oldHighlighters.sortWith(
                Comparator.comparing { highlighter: RangeHighlighter -> !highlighter.isValid }
                    .thenComparing(Segment.BY_START_OFFSET_THEN_END_OFFSET)
            )

            var curHighlight = 0
            while (curRange < myRanges.size && curHighlight < oldHighlighters.size) {
                val range = myRanges[curRange]
                val highlighter = oldHighlighters[curHighlight]
                if (!highlighter.isValid) break

                val cmp = compare(range, highlighter)
                when {
                    cmp < 0 -> {
                        newHighlighters.add(createHighlighter(mm, range, myRainbowInfoByRange.getValue(range)))
                        markDirty(range.startOffset, range.endOffset)
                        curRange++
                    }
                    cmp > 0 -> {
                        markDirty(highlighter.startOffset, highlighter.endOffset)
                        highlighter.dispose()
                        curHighlight++
                    }
                    else -> {
                        val nextInfo = myRainbowInfoByRange.getValue(range)
                        val currentRenderer = highlighter.customRenderer as? RainbowBracketPairGuideRenderer
                        val unchanged = currentRenderer != null &&
                            currentRenderer.rainbowInfo.level == nextInfo.level &&
                            currentRenderer.rainbowInfo.color == nextInfo.color
                        if (!unchanged) {
                            highlighter.customRenderer = RainbowBracketPairGuideRenderer(nextInfo)
                            markDirty(range.startOffset, range.endOffset)
                        }
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
                val range = myRanges[i]
                newHighlighters.add(createHighlighter(mm, range, myRainbowInfoByRange.getValue(range)))
                markDirty(range.startOffset, range.endOffset)
            }
        }

        myEditor.putUserData(BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY, newHighlighters)
        if (dirtyEndOffset >= dirtyStartOffset) {
            repaintOffsetsRange(dirtyStartOffset, dirtyEndOffset)
        }
    }

    private fun nowStamp(): Long {
        if (!isBracketPairGuidesShown()) return -1L
        return myEditor.getUserData(BRACKET_STRUCTURE_STAMP_KEY) ?: document.modificationStamp
    }

    companion object {
        internal val BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY = Key.create<MutableList<RangeHighlighter>>("_BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY_")
        private val LAST_TIME_GUIDES_BUILT = Key.create<Long>("_LAST_TIME_BRACKET_PAIR_GUIDES_BUILT_")
        private val CARET_REPAINT_LISTENER_INSTALLED = Key.create<Boolean>("_RB_CARET_REPAINT_LISTENER_INSTALLED_")
        private val DOC_STRUCTURE_LISTENER_INSTALLED = Key.create<Boolean>("_RB_BRACKET_GUIDES_DOC_STRUCTURE_LISTENER_INSTALLED_")
        private val BRACKET_STRUCTURE_STAMP_KEY = Key.create<Long>("_RB_BRACKET_GUIDES_STRUCTURE_STAMP_")
        private val STRUCTURE_DEBOUNCE_TIMER_KEY = Key.create<Timer>("_RB_BRACKET_STRUCTURE_DEBOUNCE_TIMER_")
        private const val BRACKET_STRUCTURE_CHARS = "()[]{}<>"
        private const val STRUCTURE_DEBOUNCE_MS = 40

        private fun isBracketPairGuidesShown(): Boolean {
            return RainbowSettings.instance.isRainbowEnabled && RainbowSettings.instance.isShowRainbowIndentGuides
        }

        private fun createHighlighter(mm: MarkupModel, range: TextRange, rainbowInfo: RainbowInfo): RangeHighlighter {
            return mm.addRangeHighlighter(
                range.startOffset,
                range.endOffset,
                0,
                null,
                HighlighterTargetArea.EXACT_RANGE
            ).apply {
                customRenderer = RainbowBracketPairGuideRenderer(rainbowInfo)
            }
        }

        private fun compare(range: TextRange, highlighter: RangeHighlighter): Int {
            val answer = range.startOffset - highlighter.startOffset
            return if (answer != 0) answer else range.endOffset - highlighter.endOffset
        }

        private fun containsBracketToken(fragment: CharSequence): Boolean {
            for (char in fragment) {
                if (BRACKET_STRUCTURE_CHARS.indexOf(char) >= 0) return true
            }
            return false
        }
    }

    private fun ensureCaretRepaintListenerInstalled() {
        if (myEditor.getUserData(CARET_REPAINT_LISTENER_INSTALLED) == true) return
        myEditor.caretModel.addCaretListener(object : CaretListener {
            override fun caretPositionChanged(event: CaretEvent) {
                val oldActive = RainbowBracketPairGuideRenderer.Companion.peekActivePair(myEditor)
                RainbowBracketPairGuideRenderer.invalidateActivePairCache(myEditor)
                val newActive = RainbowBracketPairGuideRenderer.Companion.getActivePair(myEditor)
                repaintGuide(oldActive)
                repaintGuide(newActive)
            }
        })
        myEditor.putUserData(CARET_REPAINT_LISTENER_INSTALLED, true)
    }

    private fun ensureDocumentStructureListenerInstalled() {
        if (myEditor.getUserData(DOC_STRUCTURE_LISTENER_INSTALLED) == true) return
        myEditor.putUserData(BRACKET_STRUCTURE_STAMP_KEY, document.modificationStamp)
        val debounceTimer = Timer(STRUCTURE_DEBOUNCE_MS) {
            if (!isBracketPairGuidesShown()) return@Timer
            bumpStructureStamp()
        }.apply {
            isRepeats = false
        }
        myEditor.putUserData(STRUCTURE_DEBOUNCE_TIMER_KEY, debounceTimer)
        document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                if (!isBracketPairGuidesShown()) return
                if (!isBracketStructureChange(event)) return
                if (oldOrNewContainsNewline(event)) {
                    // Newline is the most visible case for pair-guides (single-line -> multi-line).
                    // Handle it eagerly to avoid delayed appearance while avoiding daemon restart flicker.
                    myEditor.getUserData(STRUCTURE_DEBOUNCE_TIMER_KEY)?.stop()
                    bumpStructureStamp()
                    repaintCaretNeighborhood()
                } else {
                    myEditor.getUserData(STRUCTURE_DEBOUNCE_TIMER_KEY)?.restart()
                }
            }
        })
        myEditor.putUserData(DOC_STRUCTURE_LISTENER_INSTALLED, true)
    }

    private fun isBracketStructureChange(event: DocumentEvent): Boolean {
        if (oldOrNewContainsNewline(event)) return true
        val oldFragment = event.oldFragment
        val newFragment = event.newFragment
        return containsBracketToken(oldFragment) ||
            containsBracketToken(newFragment)
    }

    private fun oldOrNewContainsNewline(event: DocumentEvent): Boolean {
        val oldFragment = event.oldFragment
        val newFragment = event.newFragment
        return oldFragment.contains('\n') || newFragment.contains('\n')
    }

    private fun bumpStructureStamp() {
        val current = myEditor.getUserData(BRACKET_STRUCTURE_STAMP_KEY) ?: 0L
        myEditor.putUserData(BRACKET_STRUCTURE_STAMP_KEY, current + 1L)
    }

    private fun repaintCaretNeighborhood() {
        val caretOffset = myEditor.caretModel.offset.coerceIn(0, document.textLength)
        val line = document.getLineNumber(caretOffset)
        val startLine = (line - 1).coerceAtLeast(0)
        val endLine = (line + 1).coerceAtMost(document.lineCount - 1)
        val startOffset = document.getLineStartOffset(startLine)
        val endOffset = document.getLineEndOffset(endLine)
        repaintOffsetsRange(startOffset, endOffset)
    }

    private fun repaintGuide(highlighter: RangeHighlighter?) {
        if (highlighter == null || !highlighter.isValid) return
        val startLine = document.getLineNumber(highlighter.startOffset.coerceIn(0, document.textLength))
        val endOffset = highlighter.endOffset.coerceIn(0, document.textLength)
        val endLine = document.getLineNumber(endOffset)
        val y1 = myEditor.logicalPositionToXY(com.intellij.openapi.editor.LogicalPosition(startLine, 0)).y
        val y2 = myEditor.logicalPositionToXY(com.intellij.openapi.editor.LogicalPosition(endLine + 1, 0)).y
        val height = (y2 - y1).coerceAtLeast(myEditor.lineHeight)
        myEditor.contentComponent.repaint(0, y1, myEditor.contentComponent.width, height)
    }

    private fun repaintOffsetsRange(startOffset: Int, endOffset: Int) {
        val safeStart = startOffset.coerceIn(0, document.textLength)
        val safeEnd = endOffset.coerceIn(safeStart, document.textLength)
        val startLine = document.getLineNumber(safeStart)
        val endLine = document.getLineNumber(safeEnd)
        val y1 = myEditor.logicalPositionToXY(com.intellij.openapi.editor.LogicalPosition(startLine, 0)).y
        val y2 = myEditor.logicalPositionToXY(com.intellij.openapi.editor.LogicalPosition(endLine + 1, 0)).y
        val height = (y2 - y1).coerceAtLeast(myEditor.lineHeight)
        myEditor.contentComponent.repaint(0, y1, myEditor.contentComponent.width, height)
    }
}
