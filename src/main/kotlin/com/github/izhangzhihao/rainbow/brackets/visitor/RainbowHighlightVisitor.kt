package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.getHighlightInfo
import com.github.izhangzhihao.rainbow.brackets.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.RainbowUpdateNotifyActivity.Companion.pluginId
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.util.memoizedFileExtension
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.awt.Color


abstract class RainbowHighlightVisitor : HighlightVisitor {

    private var highlightInfoHolder: HighlightInfoHolder? = null

    override fun suitableForFile(file: PsiFile): Boolean {
        return RainbowSettings.instance.isRainbowEnabled &&
                checkForBigFile(file) &&
                !RainbowSettings.instance.getLanguagesToExclude.contains(file.fileType.name) &&
                !RainbowSettings.instance.getLanguagesToExclude.contains(memoizedFileExtension(file.name)) &&
                fileIsNotHaskellOrIntelliJHaskellPluginNotEnabled(file.fileType.name)
    }

    private fun checkForBigFile(file: PsiFile): Boolean {
        if (RainbowSettings.instance.doNOTRainbowifyBigFiles && file.getLineCount() > 1000) {

            if (!bigFilesNotified) {
                bigFilesNotified = true
                val group = NotificationGroup(
                    pluginId,
                    NotificationDisplayType.BALLOON,
                    true
                )

                val notification = group.createNotification(
                    "Rainbowify big files is disabled by default",
                    "File with line count > 1000 will not rainbowify by default. If you still want to rainbowify it, please config it in <b>Settings > Other Settings > Rainbow Brackets > Do NOT rainbowify big files</b>",
                    NotificationType.INFORMATION
                )

                notification.notify(file.project)
            }

            return false
        }
        return true
    }

    private fun fileIsNotHaskellOrIntelliJHaskellPluginNotEnabled(fileType: String) =
            fileType != "Haskell" || !isIntelliJHaskellEnabled

    final override fun analyze(file: PsiFile, updateWholeFile: Boolean, holder: HighlightInfoHolder, action: Runnable)
            : Boolean {
        highlightInfoHolder = holder
        onBeforeAnalyze(file, updateWholeFile)
        action.run()
        onAfterAnalyze()
        return true
    }

    protected open fun onBeforeAnalyze(file: PsiFile, updateWholeFile: Boolean) = Unit

    protected open fun onAfterAnalyze() {
        highlightInfoHolder = null
    }

    protected fun PsiElement.setHighlightInfo(parent: PsiElement?,
                                              level: Int,
                                              startElement: PsiElement?,
                                              endElement: PsiElement?) {
        val holder = highlightInfoHolder ?: return
        @Suppress("USELESS_ELVIS") val schema = holder.colorsScheme?: EditorColorsManager.getInstance().globalScheme
        getHighlightInfo(schema, this, level)
                ?.also {
                    holder.add(it)

                    if (startElement != null || endElement != null) {
                        val color: Color? = it.forcedTextAttributes.foregroundColor
                        color?.let {
                            parent?.saveRainbowInfo(level, color, startElement, endElement)
                        }
                    }
                }
    }

    private fun PsiElement.saveRainbowInfo(level: Int,
                                           color: Color,
                                           startElement: PsiElement?,
                                           endElement: PsiElement?) {
        val rainbowInfo = RainbowInfo.RAINBOW_INFO_KEY[this]?.also {
            it.level = level
            it.color = color
        } ?: RainbowInfo(level, color).also { RainbowInfo.RAINBOW_INFO_KEY[this] = it }

        startElement?.let { rainbowInfo.startElement = it }
        endElement?.let { rainbowInfo.endElement = it }
    }

    companion object {
        val isIntelliJHaskellEnabled: Boolean by lazy {
            PluginManagerCore.getPlugin(
                    PluginId.getId("intellij.haskell"))?.isEnabled ?: false
        }
    }
}

private var bigFilesNotified = false

fun PsiElement.getLineCount(): Int {
    try {
        val doc = containingFile?.let { PsiDocumentManager.getInstance(project).getDocument(it) }
        if (doc != null) {
            val spaceRange = textRange ?: TextRange.EMPTY_RANGE

            if (spaceRange.endOffset <= doc.textLength && spaceRange.startOffset < spaceRange.endOffset) {
                val startLine = doc.getLineNumber(spaceRange.startOffset)
                val endLine = doc.getLineNumber(spaceRange.endOffset - 1)

                return endLine - startLine + 1
            }
        }
        return StringUtil.getLineBreakCount(text ?: "") + 1
    } catch (e: Throwable) {
        return 0
    }
}
