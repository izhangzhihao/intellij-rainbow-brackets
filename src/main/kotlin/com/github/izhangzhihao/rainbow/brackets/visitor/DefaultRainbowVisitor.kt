package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.isDoNOTRainbowifyBracketsWithoutContent
import com.github.izhangzhihao.rainbow.brackets.bracePairs
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.lang.BracePair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType

/**
 * DefaultRainbowVisitor
 *
 * Created by Yii.Guxing on 2018/1/23
 */
class DefaultRainbowVisitor : RainbowHighlightVisitor() {

    override fun clone(): HighlightVisitor = DefaultRainbowVisitor()

    override fun visit(element: PsiElement) {
        val type = (element as? LeafPsiElement)?.elementType ?: return

        val matching = filterPairs(type, element) ?: return

        val level = element.getBracketLevel(matching)
        if (level >= 0) {
            element.setHighlightInfo(level)
        }
    }

    companion object {
        private fun LeafPsiElement.getBracketLevel(pair: BracePair): Int = iterateBracketParents(parent, pair, -1)

        private tailrec fun iterateBracketParents(element: PsiElement?, pair: BracePair, count: Int): Int {
            if (element == null || element is PsiFile) {
                return count
            }

            var nextCount = count
            if (element.haveBrackets(pair)) {
                nextCount++
            }

            return iterateBracketParents(element.parent, pair, nextCount)
        }

        private fun PsiElement.haveBrackets(pair: BracePair): Boolean {
            if (this is LeafPsiElement) {
                return false
            }

            val leftBraceType = pair.leftBraceType
            val rightBraceType = pair.rightBraceType
            var findLeftBracket = false
            var findRightBracket = false
            var left: PsiElement? = firstChild
            var right: PsiElement? = lastChild
            while (left != right && (!findLeftBracket || !findRightBracket)) {
                val needBreak = left == null || left.nextSibling == right

                if (left is LeafPsiElement && left.elementType == leftBraceType) {
                    findLeftBracket = true
                } else {
                    left = left?.nextSibling
                }
                if (right is LeafPsiElement && right.elementType == rightBraceType) {
                    findRightBracket = true
                } else {
                    right = right?.prevSibling
                }

                if (needBreak) {
                    break
                }
            }

            return findLeftBracket && findRightBracket
        }

        private fun PsiElement.elementType(): IElementType? {
            return (this as? LeafPsiElement)?.elementType
        }

        private fun filterPairs(type: IElementType, element: LeafPsiElement): BracePair? {
            val pairs = element.language.bracePairs ?: return null
            val filterBraceType = pairs.filter { it.leftBraceType == type || it.rightBraceType == type }
            return if (filterBraceType.isEmpty()) {
                null
            } else if (!isDoNOTRainbowifyBracketsWithoutContent) {
                filterBraceType[0]
            } else {
                filterBraceType[0]
                        .takeUnless { it.leftBraceType == type && element.nextSibling?.elementType() == it.rightBraceType }
                        .takeUnless { it?.rightBraceType == type && element.prevSibling?.elementType() == it.leftBraceType }
            }
        }
    }
}