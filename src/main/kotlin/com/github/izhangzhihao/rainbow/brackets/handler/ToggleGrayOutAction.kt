package com.github.izhangzhihao.rainbow.brackets.handler

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryAdapter
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.ui.JBColor
import java.awt.Font

class ToggleGrayOutAction : AnAction(), Disposable {
    private val highlighterMap  = HashMap<Int, RangeHighlighter>()

    private val grayoutColor : JBColor by lazy { JBColor(
            Integer.decode(RainbowSettings.instance.lightGrayoutColor),
            Integer.decode(RainbowSettings.instance.darkGrayoutColor))
    }

    private var editorFactoryAdapter : EditorFactoryAdapter? = null

    override fun actionPerformed(event: AnActionEvent?) {
        if (!RainbowSettings.instance.isRainbowEnabled){
            return
        }

        val editor = event?.getData(CommonDataKeys.EDITOR) ?: return
        val fileName = FileDocumentManager.getInstance()
                .getFile(editor.document)?.name ?: return

        ensureListener()

        val fileHash = fileName.hashCode()
        val rangeHighlighter = highlighterMap[fileHash]
        val markUpModel = editor.markupModel

        if (rangeHighlighter == null){
            val length = editor.document.textLength
            markUpModel.removeAllHighlighters()
            highlighterMap[fileHash] = markUpModel.addRangeHighlighter(0, length, HighlighterLayer.SYNTAX,
                    TextAttributes(grayoutColor, null, grayoutColor, null, Font.PLAIN),
                    HighlighterTargetArea.EXACT_RANGE)

        } else {
            markUpModel.removeHighlighter(rangeHighlighter)
            highlighterMap.remove(fileHash)
        }
    }

    private fun ensureListener(){
        if (editorFactoryAdapter == null){
            editorFactoryAdapter = object : EditorFactoryAdapter(){
                override fun editorReleased(editorEvent: EditorFactoryEvent) {

                    val closedFileName = FileDocumentManager.getInstance()
                            .getFile(editorEvent.editor.document)?.name ?: return
                    val closedFileHash = closedFileName.hashCode()
                    highlighterMap.remove(closedFileHash)
                }
            }

            EditorFactory.getInstance().addEditorFactoryListener(editorFactoryAdapter as EditorFactoryAdapter, this)
        }
    }

    override fun dispose() {}
}