package com.github.izhangzhihao.rainbow.brackets.specific

import com.github.izhangzhihao.rainbow.brackets.RainbowUtils.dynamicallySelectColor
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import java.awt.Color
import java.awt.Font

object RainbowUtils {
    fun annotateUtil(element: LeafPsiElement, holder: AnnotationHolder,
                     LEFT: String, RIGHT: String, color: Array<Color>) {

        fun getBracketLevel(element: LeafPsiElement): Int {
            var level = if (element.elementType.toString() == RIGHT) 1 else 0

            tailrec fun iterateParents(currentNode: PsiElement) {

                tailrec fun iterateChildren(currentChild: PsiElement) {
                    if (currentChild is LeafPsiElement) {
                        when (currentChild.elementType.toString()) {
                            LEFT -> level++
                            RIGHT -> level--
                        }
                    }
                    if ((currentChild != currentNode) && (currentChild != currentNode.parent.lastChild)) {
                        iterateChildren(currentChild.nextSibling)
                    }
                }

                if (currentNode.parent !is PsiFile) {
                    iterateChildren(currentNode.parent.firstChild)
                    iterateParents(currentNode.parent)
                }
            }

            iterateParents(element)
            return level
        }

        fun getColor(level: Int, color: Array<Color>) = dynamicallySelectColor(level, color)

        val level = when (element.elementType.toString()) {
            LEFT, RIGHT -> getBracketLevel(element)
            else -> 0
        }
        if (level > 0) {
            holder.createInfoAnnotation(element.psi, null).enforcedTextAttributes =
                    TextAttributes(getColor(level, color), null, null, null, Font.PLAIN)
        }
    }

    fun annotateUtil(element: LeafPsiElement, holder: AnnotationHolder,
                     LEFT: IElementType, RIGHT: IElementType, color: Array<Color>) {
        annotateUtil(element, holder, LEFT.toString(), RIGHT.toString(), color)
    }
}