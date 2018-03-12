package com.github.izhangzhihao.rainbow.brackets

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.matchers.shouldBe
import org.jetbrains.kotlin.idea.KotlinFileType

class RainbowKotlinTest : LightCodeInsightFixtureTestCase() {
    fun testRainbowForKotlin() {
        val code =
                """
fun <T> filter(l: List<T>, f: (T) -> Boolean): MutableList<T> {
    val res = mutableListOf<T>()
    l.forEach { if (f(it)) { res += it  } }
    return res
}
                """.trimIndent()
        myFixture.configureByText(KotlinFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                angleLevel(0),
                                angleLevel(0),

                                roundLevel(0),
                                angleLevel(0),
                                angleLevel(0),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(0),

                                angleLevel(0),
                                angleLevel(0),

                                squigglyLevel(0),

                                angleLevel(0),
                                angleLevel(0),

                                roundLevel(0),
                                roundLevel(0),
                                squigglyLevel(1),

                                roundLevel(0),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(0),

                                squigglyLevel(2),
                                squigglyLevel(2),

                                squigglyLevel(1),


                                squigglyLevel(0)

                        )
                )
    }
}