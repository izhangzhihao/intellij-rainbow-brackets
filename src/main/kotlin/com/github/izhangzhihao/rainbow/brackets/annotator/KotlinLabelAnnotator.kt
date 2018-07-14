package com.github.izhangzhihao.rainbow.brackets.annotator

import com.github.izhangzhihao.rainbow.brackets.RainbowInfo
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.JBColor
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
        if (!RainbowSettings.instance.isRainbowifyKotlinLabel) {
            return
        }

        val target: PsiElement
        var refElement: PsiElement?
        when (element) {
            is KtLabelReferenceExpression -> {
                if ((element.lastChild as? LeafPsiElement)?.elementType == KtTokens.AT) {
                    return
                }

                target = element
                refElement = try {
                    element.reference?.resolve()
                } catch (e: Throwable) {
                    null
                }

                refElement = when (refElement) {
                    is KtBlockExpression,
                    is KtFunctionLiteral -> refElement
                    is KtFunction -> refElement.lastChild.takeIf { it is KtBlockExpression }
                    is KtClass -> refElement.lastChild.takeIf { it is KtClassBody }
                    is KtCallExpression,
                    is KtLambdaExpression -> PsiTreeUtil.findChildOfType(refElement, KtFunctionLiteral::class.java)
                    else -> null
                }
            }
            is KtLabeledExpression -> {
                target = element.firstChild.firstChild.takeIf { it is KtLabelReferenceExpression } ?: return
                refElement = element.lastChild.let {
                    when (it) {
                        is KtBlockExpression,
                        is KtFunctionLiteral -> it
                        is KtCallExpression,
                        is KtLambdaExpression -> PsiTreeUtil.findChildOfType(it, KtFunctionLiteral::class.java)
                        else -> null
                    }
                } ?: return
            }
            else -> return
        }

        refElement
                .let { RainbowInfo.RAINBOW_INFO_KEY[it]?.color ?: DEFAULT_LABEL_COLOR }
                .let {
                    holder.createInfoAnnotation(target, null)
                            .enforcedTextAttributes = TextAttributes(it, null, null, EffectType.BOXED, Font.PLAIN)
                }
    }

    companion object {
        private val DEFAULT_LABEL_COLOR = JBColor(0x4a86e8, 0x467cda)
    }
}