package com.github.izhangzhihao.rainbow.brackets

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.matchers.shouldBe

class RainbowJavaTest : LightCodeInsightFixtureTestCase() {
    fun testRainbowForJava() {
        val code =
                """
public class Test<T> {
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
                                angleLevel(0),
                                angleLevel(0),
                                squigglyLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                squigglyLevel(1),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(1),
                                roundLevel(2),
                                roundLevel(2),
                                roundLevel(1),
                                roundLevel(0),
                                squigglyLevel(1),
                                squigglyLevel(0)
                        )
                )
    }
}