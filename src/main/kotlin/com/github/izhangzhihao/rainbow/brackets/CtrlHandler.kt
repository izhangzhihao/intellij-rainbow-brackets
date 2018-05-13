package com.github.izhangzhihao.rainbow.brackets

import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import java.awt.Font
import java.awt.event.FocusEvent
import java.awt.event.KeyEvent

/**
 * CtrlHandler
 *
 * Created by Yii.Guxing on 2018/05/13
 */
class CtrlHandler : EditorEventListener {

    private var highlighting = false
    private var storedCaretOffset = -1

    override fun onKeyPressed(editor: Editor, keyEvent: KeyEvent) {
        if (keyEvent.keyCode != KeyEvent.VK_CONTROL || !keyEvent.isControlDown) {
            editor.removeHighlighter()
            return
        }

        val caretOffset = editor.caretModel.offset
        if (caretOffset == storedCaretOffset) {
            return
        }

        editor.removeHighlighter()
        storedCaretOffset = caretOffset
        if (!highlighting) {
            highlighting = editor.addHighlightAt(caretOffset)
        }
    }

    override fun onKeyReleased(editor: Editor, keyEvent: KeyEvent) {
        editor.removeHighlighter()
    }

    override fun onFocusLost(editor: Editor, focusEvent: FocusEvent) {
        editor.removeHighlighter()
    }

    private fun Editor.addHighlightAt(offset: Int): Boolean {
        val project = project ?: return false
        val psiFile = project.let { PsiDocumentManager.getInstance(it).getPsiFile(document) } ?: return false
        val rainbowInfo = psiFile.findRainbowInfoAt(offset) ?: return false

        val highlighters = ArrayList<RangeHighlighter>()
        val attributes = TextAttributes(null, null, rainbowInfo.color, EffectType.BOXED, Font.PLAIN)
        val highlightManager = HighlightManager.getInstance(project)

        highlightManager.addRangeHighlight(
                this,
                rainbowInfo.startOffset,
                rainbowInfo.endOffset,
                attributes,
                true,
                true,
                highlighters)

        KEY_REMOVE_HIGHLIGHTER_ACTION[this] = {
            highlighters.forEach { highlightManager.removeSegmentHighlighter(this, it) }
        }

        return true
    }

    private fun Editor.removeHighlighter() {
        storedCaretOffset = -1
        if (highlighting) {
            KEY_REMOVE_HIGHLIGHTER_ACTION[this]?.invoke()
            highlighting = false
        }
    }

    companion object {
        private val KEY_REMOVE_HIGHLIGHTER_ACTION: Key<() -> Unit> = Key.create("REMOVE_HIGHLIGHTER_ACTION")

        private fun PsiFile.findRainbowInfoAt(offset: Int): RainbowInfo? {
            var element = findElementAt(offset)
            var rainbowInfo: RainbowInfo? = null
            while (element != null) {
                rainbowInfo = RainbowInfo.KEY_RAINBOW[element]
                if (rainbowInfo != null && rainbowInfo.isValid()) {
                    break
                }

                element = element.parent
            }

            return rainbowInfo
        }
    }
}