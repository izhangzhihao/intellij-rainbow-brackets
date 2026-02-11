package com.github.izhangzhihao.rainbow.brackets.lite.pairguides

import com.github.izhangzhihao.rainbow.brackets.lite.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.lite.settings.RainbowSettings
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
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
import java.util.ArrayList

class RainbowBracketPairGuidesPass internal constructor(
    project: Project,
    editor: Editor,
    private val myFile: PsiFile
) : TextEditorHighlightingPass(project, editor.document, false), DumbAware {

    private val myEditor: EditorEx = editor as EditorEx

    @Volatile
    private var myRangesWithRainbowInfo: List<Pair<TextRange, RainbowInfo>> = emptyList()

    override fun doCollectInformation(progress: ProgressIndicator) {
        if (!isBracketPairGuidesShown()) {
            myRangesWithRainbowInfo = emptyList()
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
                        val range = TextRange(startOffset, endOffset)
                        uniqueRanges.putIfAbsent(range, rainbowInfo)
                    }
                }
                super.visitElement(element)
            }
        })

        val sortedRanges = uniqueRanges.entries
            .map { Pair(it.key, it.value) }
            .sortedWith { a, b -> Segment.BY_START_OFFSET_THEN_END_OFFSET.compare(a.first, b.first) }
        myRangesWithRainbowInfo = sortedRanges
    }

    override fun doApplyInformationToEditor() {
        val nowStamp = nowStamp()
        val oldStamp = myEditor.getUserData(LAST_TIME_GUIDES_BUILT)
        if (oldStamp == nowStamp) return
        myEditor.putUserData(LAST_TIME_GUIDES_BUILT, nowStamp)

        val oldHighlighters = myEditor.getUserData(BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY)
        if (oldHighlighters != null) {
            for (highlighter in oldHighlighters) {
                highlighter.dispose()
            }
            oldHighlighters.clear()
        }

        if (!isBracketPairGuidesShown()) {
            return
        }

        val newHighlighters = ArrayList<RangeHighlighter>(myRangesWithRainbowInfo.size)
        val mm = myEditor.markupModel
        for ((range, rainbowInfo) in myRangesWithRainbowInfo) {
            val highlighter = mm.addRangeHighlighter(
                range.startOffset,
                range.endOffset,
                0,
                null,
                HighlighterTargetArea.EXACT_RANGE
            )
            highlighter.customRenderer = RainbowBracketPairGuideRenderer(rainbowInfo)
            newHighlighters.add(highlighter)
        }

        myEditor.putUserData(BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY, newHighlighters)
    }

    private fun nowStamp(): Long {
        return if (isBracketPairGuidesShown()) document.modificationStamp else -1L
    }

    companion object {
        private val BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY = Key.create<MutableList<RangeHighlighter>>("_BRACKET_PAIR_HIGHLIGHTERS_IN_EDITOR_KEY_")
        private val LAST_TIME_GUIDES_BUILT = Key.create<Long>("_LAST_TIME_BRACKET_PAIR_GUIDES_BUILT_")

        private fun isBracketPairGuidesShown(): Boolean {
            return RainbowSettings.instance.isRainbowEnabled && RainbowSettings.instance.isShowRainbowIndentGuides
        }
    }
}
