package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.getHighlightInfo
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.isRainbowEnabled
import com.github.izhangzhihao.rainbow.brackets.RainbowInfo
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.awt.Color

/**
 * RainbowHighlightVisitor
 *
 * Created by Yii.Guxing on 2018/1/24
 */
abstract class RainbowHighlightVisitor : HighlightVisitor {

    private var highlightInfoHolder: HighlightInfoHolder? = null

    private var isCalled = false

    override fun suitableForFile(file: PsiFile): Boolean = isRainbowEnabled

    @Suppress("OverridingDeprecatedMember")
    final override fun order(): Int = 1

    final override fun analyze(file: PsiFile, updateWholeFile: Boolean, holder: HighlightInfoHolder, action: Runnable)
            : Boolean {
        highlightInfoHolder = holder
        onBeforeAnalyze(file, updateWholeFile)
        try {
            action.run()
        } finally {
            call(::onAfterAnalyze)
        }

        return true
    }

    protected open fun onBeforeAnalyze(file: PsiFile, updateWholeFile: Boolean) = Unit

    protected open fun onAfterAnalyze() {
        isCalled = true
        highlightInfoHolder = null
    }

    private inline fun call(block: () -> Unit) {
        isCalled = false
        block()
        check(isCalled) { "Overriding method must invoke super." }
    }

    protected fun PsiElement.setHighlightInfo(parent: PsiElement?,
                                              level: Int,
                                              startElement: PsiElement?,
                                              endElement: PsiElement?) {
        getHighlightInfo(this, level)
                ?.also {
                    highlightInfoHolder?.add(it)

                    if (startElement != null || endElement != null) {
                        val color = it.forcedTextAttributes.foregroundColor
                        parent?.saveRainbowInfo(level, color, startElement, endElement)
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
}