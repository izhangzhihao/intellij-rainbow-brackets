package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.isEnableRainbowAngleBrackets
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType

/**
 * XmlRainbowVisitor
 *
 * Created by Yii.Guxing on 2018/01/24
 */
open class XmlRainbowVisitor : RainbowHighlightVisitor() {

    override fun suitableForFile(file: PsiFile)
            : Boolean = super.suitableForFile(file) &&
            isEnableRainbowAngleBrackets &&
            (file is XmlFile || file.viewProvider.allFiles.any { it is XmlFile })

    override fun clone(): HighlightVisitor = XmlRainbowVisitor()

    override fun visit(element: PsiElement) {
        if (element !is XmlToken) {
            return
        }

        val tokenType = element.tokenType
        when (tokenType) {
            XmlTokenType.XML_DOCTYPE_START,
            XmlTokenType.XML_DOCTYPE_END,
            XmlTokenType.XML_PI_START,
            XmlTokenType.XML_PI_END -> {
                val startElement = element.takeIf {
                    tokenType == XmlTokenType.XML_DOCTYPE_START || tokenType == XmlTokenType.XML_PI_START
                }
                val endElement = element.takeIf {
                    tokenType == XmlTokenType.XML_DOCTYPE_END || tokenType == XmlTokenType.XML_PI_END
                }
                element.setHighlightInfo(element.xmlParent, 0, startElement, endElement)
            }

            XmlTokenType.XML_START_TAG_START,
            XmlTokenType.XML_END_TAG_START,
            XmlTokenType.XML_TAG_END,
            XmlTokenType.XML_EMPTY_ELEMENT_END -> {
                val startElement = element.takeIf { tokenType == XmlTokenType.XML_START_TAG_START }
                val endElement = element.takeIf {
                    tokenType == XmlTokenType.XML_TAG_END || tokenType == XmlTokenType.XML_EMPTY_ELEMENT_END
                }
                element.level?.let { element.setHighlightInfo(element.xmlParent, it, startElement, endElement) }
            }

            XmlTokenType.XML_CDATA_START,
            XmlTokenType.XML_CDATA_END -> {
                val startElement = element.takeIf { tokenType == XmlTokenType.XML_CDATA_START }
                val endElement = element.takeIf { tokenType == XmlTokenType.XML_CDATA_END }
                element.level?.let { element.setHighlightInfo(element.parent, it + 1, startElement, endElement) }
            }
        }
    }

    companion object {
        private val PsiElement.xmlParent: PsiElement?
            get() {
                var pElement = parent
                while (pElement != null && pElement !is XmlTag && pElement !is PsiFile) {
                    pElement = pElement.parent
                }

                return pElement
            }

        private tailrec fun iterateXmlTagParents(element: PsiElement?, count: Int): Int {
            if (element == null || element is PsiFile) {
                return count
            }

            var nextCount = count
            if (element is XmlTag) {
                nextCount++
            }

            return iterateXmlTagParents(element.parent, nextCount)
        }

        private val XmlToken.level: Int?
            get() = iterateXmlTagParents(parent, -1).takeIf { it >= 0 }
    }
}