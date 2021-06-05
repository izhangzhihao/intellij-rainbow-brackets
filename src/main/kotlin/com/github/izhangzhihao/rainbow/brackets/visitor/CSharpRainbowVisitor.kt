package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.lang.BracePair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.GT
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.LPARENTH
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.LT
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.RPARENTH
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.psi.CSharpDummyNode


class CSharpRainbowVisitor : RainbowHighlightVisitor() {

    override fun suitableForFile(file: PsiFile)
            : Boolean = super.suitableForFile(file) &&
            RainbowSettings.instance.isEnableRainbowAngleBrackets &&
            (file.language.id == "C#" ||
                    file.viewProvider.allFiles.any { it.language.id == "C#" }
                    )

    override fun clone(): HighlightVisitor = CSharpRainbowVisitor()

    override fun visit(element: PsiElement) {
        val type = (element as? LeafPsiElement)?.elementType ?: return
        val pair = map[type]
        if (pair != null) {
            val level = element.getBracketLevel(pair, type)
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
        val map = mapOf(
                LPARENTH to BracePair(LPARENTH, RPARENTH, true),
                RPARENTH to BracePair(LPARENTH, RPARENTH, true),
                //LT to BracePair(LT, GT, true),
                //GT to BracePair(LT, GT, true),
        )

        private fun LeafPsiElement.getBracketLevel(pair: BracePair, type: IElementType): Int = iterateBracketParents(this, pair, -1, type)

        private tailrec fun iterateBracketParents(element: PsiElement?, pair: BracePair, count: Int, type: IElementType): Int {
            if (element == null || element is PsiFile) {
                return count
            }

            var nextCount = count

            if (element is LeafPsiElement && type == pair.leftBraceType && element.elementType == pair.rightBraceType) {
                nextCount--
            }

            if (element is LeafPsiElement && type == pair.rightBraceType && element.elementType == pair.leftBraceType) {
                nextCount--
            }

            if (element is LeafPsiElement && element.elementType == type) {
                nextCount++
            }

            return if (type == pair.leftBraceType) {
                val prev = element.prevSibling
                if (prev == null) {
                    iterateBracketParents(element.parent.prevSibling.iterForPreDummyNode()?.lastChild, pair, nextCount, type)
                } else {
                    iterateBracketParents(prev, pair, nextCount, type)
                }
            } else {
                val next = element.nextSibling
                if (next == null) {
                    iterateBracketParents(element.parent.nextSibling.iterForNextDummyNode()?.firstChild, pair, nextCount, type)
                } else {
                    iterateBracketParents(next, pair, nextCount, type)
                }
            }
        }

        private tailrec fun PsiElement?.iterForNextDummyNode(): PsiElement? {
            return if (this == null) {
                null
            } else if (this is CSharpDummyNode) {
                this
            } else {
                if (this.nextSibling == null) {
                    null
                } else {
                    this.nextSibling.iterForNextDummyNode()
                }
            }
        }


        private tailrec fun PsiElement?.iterForPreDummyNode(): PsiElement? {
            return if (this == null) {
                null
            } else if (this is CSharpDummyNode) {
                this
            } else {
                if (this.prevSibling == null) {
                    null
                } else {
                    this.prevSibling.iterForPreDummyNode()
                }
            }
        }
    }
}