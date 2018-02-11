package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.defaultRoundBracketsColors
import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter.defaultSquigglyBracketsColors
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.matchers.shouldBe

class RainbowJavaTest : LightCodeInsightFixtureTestCase() {
    fun testRainbowForJava() {
        val code =
                """
public class Test {
    public Boolean test() {
        return "true".equals("false") || "false".equals("true".concat("false".length() + "*"));
    }
}
                """.trimIndent()
        myFixture.configureByText("dummy.java", code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributes.foregroundColor.rgb }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                defaultSquigglyBracketsColors[0].rgb,
                                defaultRoundBracketsColors[0].rgb,
                                defaultRoundBracketsColors[0].rgb,
                                defaultSquigglyBracketsColors[1].rgb,
                                defaultRoundBracketsColors[0].rgb,
                                defaultRoundBracketsColors[0].rgb,
                                defaultRoundBracketsColors[0].rgb,
                                defaultRoundBracketsColors[1].rgb,
                                defaultRoundBracketsColors[2].rgb,
                                defaultRoundBracketsColors[2].rgb,
                                defaultRoundBracketsColors[1].rgb,
                                defaultRoundBracketsColors[0].rgb,
                                defaultSquigglyBracketsColors[1].rgb,
                                defaultSquigglyBracketsColors[0].rgb
                        )
                )
    }
}