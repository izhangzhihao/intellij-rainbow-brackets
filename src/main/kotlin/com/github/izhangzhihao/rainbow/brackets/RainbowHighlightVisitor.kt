package com.github.izhangzhihao.rainbow.brackets

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.lang.BracePair
import com.intellij.lang.LanguageBraceMatching
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.Stack
import java.awt.Font

typealias StackMap = MutableMap<BracePair, Stack<PsiElement>>
typealias ElementPairsMap = MutableMap<BracePair, MutableList<ElementPair>>

data class ElementPair(val left: PsiElement, val right: PsiElement, val level: Int)

/**
 * RainbowHighlightVisitor
 *
 * Created by Yii.Guxing on 2018/1/23
 */
class RainbowHighlightVisitor : HighlightVisitor {

    private var highlightInfoHolder: HighlightInfoHolder? = null
    private var bracePairs: Array<BracePair>? = null

    private val stackMap: StackMap = mutableMapOf()
    private val elementPairsMap: ElementPairsMap = mutableMapOf()

    override fun suitableForFile(file: PsiFile): Boolean = true

    override fun clone(): HighlightVisitor = RainbowHighlightVisitor()

    @Suppress("OverridingDeprecatedMember")
    override fun order(): Int = 1

    override fun analyze(file: PsiFile,
                         updateWholeFile: Boolean,
                         holder: HighlightInfoHolder,
                         action: Runnable): Boolean {
        val braceMatcher = LanguageBraceMatching.INSTANCE.forLanguage(file.language) ?: return false

        bracePairs = braceMatcher.pairs
        highlightInfoHolder = holder

        try {
            action.run()
            bracePairs?.forEach { it.tint() }
        } finally {
            clear()
        }

        return true
    }

    private fun clear() {
        highlightInfoHolder = null
        bracePairs = null

        stackMap.clear()
        elementPairsMap.clear()
    }

    override fun visit(element: PsiElement) {
        val pairs = bracePairs ?: return
        val type = element.node?.elementType ?: return
        val pair = pairs.find { it.leftBraceType == type || it.rightBraceType == type } ?: return

        if (pair.leftBraceType == type) {
            val stack = stackMap[pair] ?: Stack<PsiElement>().also { stackMap[pair] = it }

            if (stack.isNotEmpty()) {
                val headerParent = stack.first().parent
                val parent = PsiTreeUtil.findFirstParent(element, true) { it == headerParent }
                if (parent == null) {
                    pair.tint()
                }
            }

            stack.push(element)
        } else {
            val stack = stackMap[pair]
            if (stack != null && stack.isNotEmpty()) {
                val headerParent = stack.first().parent
                var level = -1
                val parent = PsiTreeUtil.findFirstParent(element, true) {
                    level++
                    it == headerParent
                }
                if (parent != null) {
                    val elementPairs = elementPairsMap[pair]
                            ?: mutableListOf<ElementPair>().also { elementPairsMap[pair] = it }
                    elementPairs.add(ElementPair(stack.pop(), element, level))

                    if (stack.isEmpty()) {
                        pair.tint()
                    }
                }
            }
        }

    }

    private fun BracePair.tint() {
        val holder = highlightInfoHolder ?: return
        elementPairsMap[this]?.forEach {
            holder.add(HighlightInfo.newHighlightInfo(RAINBOW_ELEMENT).textAttributes(TextAttributes(RainbowUtils.dynamicallySelectColor(it.level, RainbowColors.roundBracketsColor), null, null, null, Font.PLAIN)).range(it.left).create())
            holder.add(HighlightInfo.newHighlightInfo(RAINBOW_ELEMENT).textAttributes(TextAttributes(RainbowUtils.dynamicallySelectColor(it.level, RainbowColors.roundBracketsColor), null, null, null, Font.PLAIN)).range(it.right).create())
        }
        stackMap[this]?.clear()
    }

    companion object {
        private val RAINBOW_ELEMENT: HighlightInfoType = HighlightInfoType.HighlightInfoTypeImpl(HighlightSeverity.INFORMATION, DefaultLanguageHighlighterColors.CONSTANT)

    }
}