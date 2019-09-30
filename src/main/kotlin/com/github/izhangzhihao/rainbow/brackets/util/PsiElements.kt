package com.github.izhangzhihao.rainbow.brackets.util

import com.intellij.psi.PsiElement

// Fix: java.lang.NoClassDefFoundError: org/jetbrains/kotlin/psi/psiUtil/PsiUtilsKt
val PsiElement.startOffset: Int get() = textRange.startOffset

// Fix: java.lang.NoClassDefFoundError: org/jetbrains/kotlin/psi/psiUtil/PsiUtilsKt
val PsiElement.endOffset: Int get() = textRange.endOffset

tailrec fun PsiElement.findPrevSibling(condition: (PsiElement) -> Boolean): PsiElement? {
    val prevSibling = prevSibling ?: return null
    return if (condition(prevSibling)) {
        prevSibling
    } else {
        prevSibling.findPrevSibling(condition)
    }
}

tailrec fun PsiElement.findNextSibling(condition: (PsiElement) -> Boolean): PsiElement? {
    val nextSibling = nextSibling ?: return null
    return if (condition(nextSibling)) {
        nextSibling
    } else {
        nextSibling.findNextSibling(condition)
    }
}