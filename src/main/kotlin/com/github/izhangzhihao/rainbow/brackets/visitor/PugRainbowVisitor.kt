package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.visitor.XmlRainbowVisitor.Companion.xmlParent
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType


class PugRainbowVisitor : RainbowHighlightVisitor() {

    override fun suitableForFile(file: PsiFile)
            : Boolean = super.suitableForFile(file) &&
            RainbowSettings.instance.isEnableRainbowAngleBrackets &&
            (file.language.id == "Jade")

    override fun clone(): HighlightVisitor = PugRainbowVisitor()

    override fun visit(element: PsiElement) {
        if (element !is XmlToken) {
            return
        }

        if (element.tokenType == XmlTokenType.XML_TAG_NAME) {
            val parent = element.parent
            if (parent != null && parent is XmlTag) {
                parent.level.let { element.setHighlightInfo(element.xmlParent, it, element, null) }
            }
        }
    }

    companion object {
        private tailrec fun iterateXmlTagParents(element: PsiElement?, count: Int, name: String): Int {
            if (element == null || element is PsiFile || (element is XmlTag && element.name != name)) {
                return count
            }

            var nextCount = count
            if (element is XmlTag && element.name == name) {
                nextCount++
            }

            return iterateXmlTagParents(element.parent, nextCount, name)
        }

        private val XmlTag.level: Int
            get() = iterateXmlTagParents(parent, 0, this.name)
    }
}