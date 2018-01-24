package com.github.izhangzhihao.rainbow.brackets

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.lang.BracePair
import com.intellij.lang.LanguageBraceMatching
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.Stack

/**
 * DefaultRainbowVisitor
 *
 * Created by Yii.Guxing on 2018/1/23
 */
class DefaultRainbowVisitor : RainbowHighlightVisitor() {

    private var bracePairs: Array<BracePair>? = null

    private val stackMap: MutableMap<BracePair, Stack<PsiElement>> = mutableMapOf()

    override fun suitableForFile(file: PsiFile)
            : Boolean = LanguageBraceMatching.INSTANCE.forLanguage(file.language) != null

    override fun clone(): HighlightVisitor = DefaultRainbowVisitor()

    override fun onBeforeAnalyze(file: PsiFile, updateWholeFile: Boolean) {
        bracePairs = LanguageBraceMatching.INSTANCE.forLanguage(file.language)?.pairs
    }

    override fun onAfterAnalyze() {
        super.onAfterAnalyze()
        bracePairs = null
        stackMap.clear()
    }

    override fun visit(element: PsiElement) {
        val pairs = bracePairs ?: return
        val type = element.node?.elementType ?: return
        val pair = pairs.find { it.leftBraceType == type || it.rightBraceType == type } ?: return

        if (pair.leftBraceType == type) {
            val stack = stackMap[pair] ?: Stack<PsiElement>().also { stackMap[pair] = it }
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
                    holder?.add(getInfo(stack.pop(), level))
                    holder?.add(getInfo(element, level))
                }
            }
        }
    }
}