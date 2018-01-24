package com.github.izhangzhihao.rainbow.brackets

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.awt.Font

/**
 * RainbowHighlightVisitor
 *
 * Created by Yii.Guxing on 2018/1/24
 */
abstract class RainbowHighlightVisitor : HighlightVisitor {

    private var _holder: HighlightInfoHolder? = null
    protected val holder: HighlightInfoHolder? get() = _holder

    private var isCalled = false

    @Suppress("OverridingDeprecatedMember")
    final override fun order(): Int = 1

    final override fun analyze(file: PsiFile, updateWholeFile: Boolean, holder: HighlightInfoHolder, action: Runnable)
            : Boolean {
        _holder = holder
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
        _holder = null
    }

    private inline fun call(block: () -> Unit) {
        isCalled = false
        block()
        check(isCalled) { "Overriding method must invoke super." }
    }

    companion object {
        private val RAINBOW_ELEMENT: HighlightInfoType = HighlightInfoType
                .HighlightInfoTypeImpl(HighlightSeverity.INFORMATION, DefaultLanguageHighlighterColors.CONSTANT)

        fun getInfo(element: PsiElement, level: Int): HighlightInfo? = HighlightInfo
                .newHighlightInfo(RAINBOW_ELEMENT)
                .textAttributes(TextAttributes(RainbowUtils.dynamicallySelectColor(level, RainbowColors.roundBracketsColor), null, null, null, Font.PLAIN))
                .range(element)
                .create()
    }
}