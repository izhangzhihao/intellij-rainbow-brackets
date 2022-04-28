package com.github.izhangzhihao.rainbow.brackets.annotator

import com.github.izhangzhihao.rainbow.brackets.RainbowInfo
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import java.awt.Font


class KotlinLambdaExpressionArrowAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if ((element as? LeafPsiElement)?.elementType == KtTokens.ARROW) {
            RainbowInfo.RAINBOW_INFO_KEY[element.parent]?.color?.let {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element)
                    .textAttributes(
                        com.github.izhangzhihao.rainbow.brackets.util.create(
                            "rainbow-kotlin-arrow",
                            TextAttributes(it, null, null, EffectType.BOXED, Font.PLAIN)
                        )
                    )
                    .create()
            }
        }
    }
}