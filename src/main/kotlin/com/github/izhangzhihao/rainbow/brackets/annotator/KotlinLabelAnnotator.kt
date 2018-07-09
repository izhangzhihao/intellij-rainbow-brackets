package com.github.izhangzhihao.rainbow.brackets.annotator

import com.github.izhangzhihao.rainbow.brackets.RainbowInfo
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import java.awt.Font

/**
 * KotlinLabelAnnotator
 *
 * Created by Yii.Guxing on 2018/07/09
 */
class KotlinLabelAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is KtLabelReferenceExpression) {
            var refElement = element.reference?.resolve()
            if (refElement == null && (element.lastChild as? LeafPsiElement)?.elementType == KtTokens.AT) {
                // FIXME 由于这个时候默认的着色器还未遍历到后面的元素，因此这里会着色失败。
                refElement = element.parent?.parent?.lastChild?.lastChild
            } else if (refElement != null && refElement is KtClass) {
                refElement = refElement.lastChild
            }

            refElement
                    ?.let { RainbowInfo.RAINBOW_INFO_KEY[it] }
                    ?.color
                    ?.let {
                        holder.createInfoAnnotation(element, null)
                                .enforcedTextAttributes = TextAttributes(it, null, null, EffectType.BOXED, Font.PLAIN)
                    }
        }
    }
}