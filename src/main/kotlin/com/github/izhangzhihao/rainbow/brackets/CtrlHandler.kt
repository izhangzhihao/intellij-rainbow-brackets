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
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.awt.Color
import java.awt.Font
import java.awt.event.FocusEvent
import java.awt.event.KeyEvent
import java.util.*

/**
 * CtrlHandler
 *
 * Created by Yii.Guxing on 2018/05/13
 */
class CtrlHandler : EditorEventListener {

    private var settings = RainbowSettings.instance

    private var highlighting = false
    private var restraining = false
    private var storedCaretOffset = -1

    override fun onKeyPressed(editor: Editor, keyEvent: KeyEvent) {
        if (!settings.isEnableHighlightCurrentScopeWhenCtrlPressed) {
            return
        }

        val controlOrMetaDown = keyEvent.isControlOrMetaKeyDown
        val altDown = keyEvent.isAltKayDown
        if (controlOrMetaDown == altDown) {
            editor.removeHighlighter()
            return
        }

        val caretOffset = editor.caretModel.offset
        if (highlighting == controlOrMetaDown && restraining == altDown && caretOffset == storedCaretOffset) {
            return
        }

        addHighlighter(editor, caretOffset, altDown)
    }

    override fun onKeyReleased(editor: Editor, keyEvent: KeyEvent) {
        editor.removeHighlighter()
    }

    override fun onFocusLost(editor: Editor, focusEvent: FocusEvent) {
        editor.removeHighlighter()
    }

    private fun addHighlighter(editor: Editor, caretOffset: Int, restrainOutside: Boolean) {
        editor.removeHighlighter()
        storedCaretOffset = caretOffset
        val added = editor.addHighlighterAt(caretOffset, restrainOutside)
        if (restrainOutside) {
            restraining = added
        } else {
            highlighting = added
        }
    }

    private fun Editor.addHighlighterAt(offset: Int, restrainOutside: Boolean): Boolean {
        val project = project ?: return false
        val psiFile = project.let { PsiDocumentManager.getInstance(it).getPsiFile(document) } ?: return false
        val rainbowInfo = psiFile.findRainbowInfoAt(offset) ?: return false

        val defaultBackground = EditorColorsManager.getInstance().globalScheme.defaultBackground
        val highlightManager = HighlightManager.getInstance(project)
        val highlighters = LinkedList<RangeHighlighter>()

        if (restrainOutside) {
            val background = Color.GRAY.alphaBlend(defaultBackground, 0.06f)
            val foreground = Color.GRAY.alphaBlend(defaultBackground, 0.55f)
            val attributes = TextAttributes(foreground, background, background, EffectType.BOXED, Font.PLAIN)
            val startOffset = rainbowInfo.startOffset
            val endOffset = rainbowInfo.endOffset
            if (startOffset > 0) {
                highlightManager.addRangeHighlight(this,
                        0,
                        startOffset,
                        attributes,
                        true,
                        true,
                        highlighters)
            }
            val lastOffset = document.textLength
            if (endOffset < lastOffset) {
                highlightManager.addRangeHighlight(this,
                        endOffset,
                        lastOffset,
                        attributes,
                        true,
                        true,
                        highlighters)
            }
        } else {
            val background = rainbowInfo.color.alphaBlend(defaultBackground, 0.2f)
            val attributes = TextAttributes(null, background, rainbowInfo.color, EffectType.BOXED, Font.PLAIN)
            highlightManager.addRangeHighlight(this,
                    rainbowInfo.startOffset,
                    rainbowInfo.endOffset,
                    attributes,
                    true,
                    true,
                    highlighters)
        }

        if (highlighters.isNotEmpty()) {
            KEY_REMOVE_HIGHLIGHTER_ACTION[this] = {
                highlighters.forEach { highlightManager.removeSegmentHighlighter(this, it) }
            }
        }

        return true
    }

    private fun Editor.removeHighlighter() {
        if (highlighting || restraining) {
            storedCaretOffset = -1
            KEY_REMOVE_HIGHLIGHTER_ACTION[this]?.invoke()
            putUserData(KEY_REMOVE_HIGHLIGHTER_ACTION, null)
            highlighting = false
            restraining = false
        }
    }

    companion object {
        private val KEY_REMOVE_HIGHLIGHTER_ACTION: Key<() -> Unit> = Key.create("REMOVE_HIGHLIGHTER_ACTION")

        private val KeyEvent.isControlOrMetaKeyDown: Boolean
            get() = if (SystemInfo.isMac) {
                isMetaDown && keyCode == KeyEvent.VK_META
            } else {
                isControlDown && keyCode == KeyEvent.VK_CONTROL
            }

        private val KeyEvent.isAltKayDown: Boolean
            get() = isAltDown && keyCode == KeyEvent.VK_ALT

        private fun PsiFile.findRainbowInfoAt(offset: Int): RainbowInfo? {
            var element = findElementAt(offset)
            while (element != null) {
                element.getRainbowInfo(offset)?.let { return it }
                element = element.parent
            }

            return null
        }

        private fun PsiElement.getRainbowInfo(offset: Int): RainbowInfo? {
            return RainbowInfo.KEY_RAINBOW[this]?.takeIf { it.containsOffset(offset) }
        }

        private fun Color.alphaBlend(background: Color, alpha: Float): Color {
            val r = (1 - alpha) * background.red + alpha * red
            val g = (1 - alpha) * background.green + alpha * green
            val b = (1 - alpha) * background.blue + alpha * blue

            return Color(r.toInt(), g.toInt(), b.toInt())
        }
    }
}