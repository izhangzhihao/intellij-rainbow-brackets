package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.visitor.XmlRainbowVisitor.Companion.iterateXmlTagParents
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
            (file.language.id == "Jade" ||
                    file.viewProvider.allFiles.any { it.language.id == "Jade" } ||
                    file.name.endsWith(".vue")
                    )

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
        private val XmlTag.level: Int
            get() = iterateXmlTagParents(parent, 0)
    }
}