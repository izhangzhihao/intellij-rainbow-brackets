package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.SystemInfo
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import java.awt.Color
import java.awt.Font
import java.awt.event.FocusEvent
import java.awt.event.KeyEvent

/**
 * CtrlHandler
 *
 * Created by Yii.Guxing on 2018/05/13
 */
class CtrlHandler : EditorEventListener {

    private var settings = RainbowSettings.instance

    private var highlighting = false
    private var storedCaretOffset = -1

    override fun onKeyPressed(editor: Editor, keyEvent: KeyEvent) {
        if (!settings.isEnableHighlightCurrentScopeWhenCtrlPressed) {
            return
        }

        if (!keyEvent.isControlOrMetaKey) {
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

    private val KeyEvent.isControlOrMetaKey: Boolean
        get() = if (SystemInfo.isMac) {
            isMetaDown && keyCode == KeyEvent.VK_META
        } else {
            isControlDown && keyCode == KeyEvent.VK_CONTROL
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

        val defaultBackground = EditorColorsManager.getInstance().globalScheme.defaultBackground
        val background = rainbowInfo.color.alphaBlend(defaultBackground, 0.2f)
        val attributes = TextAttributes(null, background, rainbowInfo.color, EffectType.BOXED, Font.PLAIN)
        val highlightManager = HighlightManager.getInstance(project)
        val highlighters = ArrayList<RangeHighlighter>()

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
            putUserData(KEY_REMOVE_HIGHLIGHTER_ACTION, null)
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

        private fun Color.alphaBlend(background: Color, alpha: Float): Color {
            val r = (1 - alpha) * background.red + alpha * red
            val g = (1 - alpha) * background.green + alpha * green
            val b = (1 - alpha) * background.blue + alpha * blue

            return Color(r.toInt(), g.toInt(), b.toInt())
        }
    }
}