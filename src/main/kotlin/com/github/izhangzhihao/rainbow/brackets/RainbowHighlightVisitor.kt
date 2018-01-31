package com.github.izhangzhihao.rainbow.brackets

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * RainbowHighlightVisitor
 *
 * Created by Yii.Guxing on 2018/1/24
 */
abstract class RainbowHighlightVisitor : HighlightVisitor {

    private var highlightInfoHolder: HighlightInfoHolder? = null

    private var isCalled = false

    override fun suitableForFile(file: PsiFile): Boolean = RainbowHighlighter.isRainbowEnabled

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

    protected fun PsiElement.setHighlightInfo(level: Int) {
        highlightInfoHolder?.add(RainbowHighlighter.getHighlightInfo(this, level))
    }
}