package com.github.izhangzhihao.rainbow.brackets.util

import com.intellij.psi.PsiElement

// Fix: java.lang.NoClassDefFoundError: org/jetbrains/kotlin/psi/psiUtil/PsiUtilsKt
val PsiElement.startOffset: Int get() = textRange.startOffset