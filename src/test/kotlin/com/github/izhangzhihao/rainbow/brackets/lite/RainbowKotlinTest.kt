package com.github.izhangzhihao.rainbow.brackets.lite

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.idea.KotlinFileType

class RainbowKotlinTest : LightJavaCodeInsightFixtureTestCase() {
    fun testKotlinFunctionLiteralBracesAndArrow() {
        @Language("kotlin") val code =
                """
val a :Int = 1

fun t() {
    a?.let {
    }
}
                """.trimIndent()
        myFixture.configureByText(KotlinFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())

        doHighlighting
                .filter { brackets.contains(it.text.toChar()) }
                .filter { it?.forcedTextAttributesKey?.defaultAttributes != null }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),
                                roundLevel(0),
                                squigglyLevel(0),
                                //squigglyLevel(1),
                                //squigglyLevel(1),
                                squigglyLevel(0)
                        )
                )
    }
}