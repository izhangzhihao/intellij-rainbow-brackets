package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.lang.BracePair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.jetbrains.python.PyTokenTypes.*
import com.jetbrains.python.psi.PyStatement


class PythonRainbowVisitor : RainbowHighlightVisitor() {

    override fun suitableForFile(file: PsiFile)
            : Boolean = super.suitableForFile(file) &&
            RainbowSettings.instance.isEnableRainbowAngleBrackets &&
            (file.language.id == "Python" ||
                    file.viewProvider.allFiles.any { it.language.id == "Python" }
                    )

    override fun clone(): HighlightVisitor = PythonRainbowVisitor()

    override fun visit(element: PsiElement) {
        val type = (element as? LeafPsiElement)?.elementType ?: return
        val pair = statementKeywords[type]
        if (pair != null) {
            val level = element.getBracketLevel(pair)
            if (RainbowSettings.instance.isDoNOTRainbowifyTheFirstLevel) {
                if (level >= 1) {
                    rainbowPairs(element, pair, level)
                }
            } else {
                if (level >= 0) {
                    rainbowPairs(element, pair, level)
                }
            }
        }
    }

    private fun rainbowPairs(element: LeafPsiElement, pair: BracePair, level: Int) {
        val startElement = element.takeIf { it.elementType == pair.leftBraceType }
        val endElement = element.takeIf { it.elementType == pair.rightBraceType }
        element.setHighlightInfo(element.parent, level, startElement, endElement)
    }

    companion object {
        val statementKeywords = mapOf(
            IF_KEYWORD to BracePair(IF_KEYWORD, ELSE_KEYWORD, true),
            ELSE_KEYWORD to BracePair(IF_KEYWORD, ELSE_KEYWORD, true),
            ELIF_KEYWORD to BracePair(ELIF_KEYWORD, ELSE_KEYWORD, true),
        )

        private fun LeafPsiElement.getBracketLevel(pair: BracePair): Int =
            iterateBracketParents(this, pair, -1)

        private tailrec fun iterateBracketParents(
            element: PsiElement?,
            pair: BracePair,
            count: Int
        ): Int {
            if (element == null || element is PsiFile) {
                return count
            }

            var nextCount = count

            if (element is PyStatement) {
                nextCount++
            }

            return iterateBracketParents(
                element.parent,
                pair,
                nextCount,
            )
        }
    }
}