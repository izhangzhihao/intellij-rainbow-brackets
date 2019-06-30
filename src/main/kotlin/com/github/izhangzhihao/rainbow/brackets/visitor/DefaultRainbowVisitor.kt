package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.bracePairs
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
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
        if (element.javaClass.simpleName == "OCMacroForeignLeafElement") return
        val type = (element as? LeafPsiElement)?.elementType ?: return

        val matching = filterPairs(type, element) ?: return

        val pair =
                if (matching.size == 1) {
                    matching[0]
                } else {
                    matching.find { element.isValidBracket(it) }
                } ?: return

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

    private fun rainbowPairs(element: LeafPsiElement, pair: BracePair, level: Int) {
        val startElement = element.takeIf { it.elementType == pair.leftBraceType }
        val endElement = element.takeIf { it.elementType == pair.rightBraceType }
        element.setHighlightInfo(element.parent, level, startElement, endElement)
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

        private fun LeafPsiElement.isValidBracket(pair: BracePair): Boolean {
            val pairType = when (elementType) {
                pair.leftBraceType -> pair.rightBraceType
                pair.rightBraceType -> pair.leftBraceType
                else -> return false
            }

            return if (pairType == pair.leftBraceType) {
                checkBracePair(this, parent.firstChild, pairType, PsiElement::getNextSibling)
            } else {
                checkBracePair(this, parent.lastChild, pairType, PsiElement::getPrevSibling)
            }
        }

        private fun checkBracePair(brace: PsiElement,
                                   start: PsiElement,
                                   type: IElementType,
                                   next: PsiElement.() -> PsiElement?): Boolean {
            var element: PsiElement? = start
            while (element != null && element != brace) {
                if (element is LeafPsiElement && element.elementType == type) {
                    return true
                }

                element = element.next()
            }

            return false
        }

        private fun filterPairs(type: IElementType, element: LeafPsiElement): List<BracePair>? {
            val pairs = element.language.bracePairs ?: return null
            val filterBraceType = pairs[type.toString()] ?: return null
            return if (filterBraceType.isEmpty()) {
                null
            } else if (!RainbowSettings.instance.isDoNOTRainbowifyBracketsWithoutContent) {
                filterBraceType
            } else {
                filterBraceType
                        .filterNot { it.leftBraceType == type && element.nextSibling?.elementType() == it.rightBraceType }
                        .filterNot { it.rightBraceType == type && element.prevSibling?.elementType() == it.leftBraceType }
            }
        }
    }
}
