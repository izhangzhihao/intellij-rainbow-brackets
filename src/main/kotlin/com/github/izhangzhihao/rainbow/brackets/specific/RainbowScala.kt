package com.github.izhangzhihao.rainbow.brackets.specific

import com.github.izhangzhihao.rainbow.brackets.RainbowColors.angleBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.roundBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squareBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowColors.squigglyBracketsColor
import com.github.izhangzhihao.rainbow.brackets.RainbowUtils.settings
import com.github.izhangzhihao.rainbow.brackets.specific.RainbowUtils.annotateUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * From https://github.com/JetBrains/intellij-scala/blob/idea173.x/scala/scala-impl/src/org/jetbrains/plugins/scala/lang/lexer/ScalaTokenTypes.java
 * tLSQBRACKET [
 * tRSQBRACKET ]
 * tLBRACE {
 * tRBRACE }
 * tLPARENTHESIS (
 * tRPARENTHESIS )
 * tLESS <
 * tGREATER >
 */
class RainbowScala : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (settings.isRainbowEnabled && element is LeafPsiElement) {
            if (settings.isEnableRainbowRoundBrackets) annotateUtil(element, holder, "(", ")", roundBracketsColor)
            if (settings.isEnableRainbowSquareBrackets) annotateUtil(element, holder, "[", "]", squareBracketsColor)
            if (settings.isEnableRainbowSquigglyBrackets) annotateUtil(element, holder, "{", "}", squigglyBracketsColor)
            if (settings.isEnableRainbowAngleBrackets) annotateUtil(element, holder, "<", ">", angleBracketsColor)
        }
    }
}