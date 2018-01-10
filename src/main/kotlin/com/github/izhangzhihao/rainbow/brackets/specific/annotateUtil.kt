package com.github.izhangzhihao.rainbow.brackets.specific

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import java.awt.Color
import java.awt.Font

/**
 * Take from https://github.com/ArtsiomCh/NestedBracketsColorer
 */
fun annotateUtil(element: PsiElement, holder: AnnotationHolder,
                 LEFT: IElementType, RIGHT: IElementType, color: Array<Color>) {

    fun getBracketLevel(element: LeafPsiElement): Int {
        var level = if (element.elementType == RIGHT) 1 else 0

        tailrec fun iterateParents(currentNode: PsiElement) {

            tailrec fun iterateChildren(currentChild: PsiElement) {
                if (currentChild is LeafPsiElement) {
                    when (currentChild.elementType) {
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

    fun getColor(level: Int) = color[(level - 1) % color.size]

    if (element is LeafPsiElement) {
        val level = when (element.elementType) {
            LEFT, RIGHT -> getBracketLevel(element)
            else -> 0
        }
        if (level > 1) {
            holder.createInfoAnnotation(element as PsiElement, null).enforcedTextAttributes =
                    TextAttributes(getColor(level), null, null, null, Font.PLAIN)
        }
    }
}