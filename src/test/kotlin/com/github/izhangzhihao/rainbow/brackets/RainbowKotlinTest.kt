package com.github.izhangzhihao.rainbow.brackets

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotlintest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.idea.KotlinFileType

class RainbowKotlinTest : LightJavaCodeInsightFixtureTestCase() {
    fun testRainbowForKotlin() {
        @Language("kotlin") val code =
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

    fun testRainbowArrowForKotlin() {
        @Language("kotlin") val code =
                """
val a: (Int) -> Unit = { aa ->
    val b: (Int) -> Unit = { bb ->
        val c: (Int) -> Unit  = { cc ->
            val d: (Int) -> Unit = { dd ->
            }
        }
    }
}
                """.trimIndent()
        myFixture.configureByText(KotlinFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting
                .filter { it.forcedTextAttributes != null }
                .map { it.forcedTextAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),
                                roundLevel(0),

                                squigglyLevel(0),
                                squigglyLevel(0),

                                roundLevel(0),
                                roundLevel(0),

                                squigglyLevel(1),
                                squigglyLevel(1),


                                roundLevel(0),
                                roundLevel(0),

                                squigglyLevel(2),
                                squigglyLevel(2),

                                roundLevel(0),
                                roundLevel(0),

                                squigglyLevel(3),
                                squigglyLevel(3),


                                squigglyLevel(3),

                                squigglyLevel(2),


                                squigglyLevel(1),

                                squigglyLevel(0)
                        )
                )
    }

    fun testRainbowLabelForKotlin() {
        @Language("kotlin") val code =
                """
class AA {
    fun aa() {
        arrayOf(1, 2, 3).forEach {
                it.let dd@{
                    if (it > 0) a@ {
                        return@dd
                    }
                }
                return@forEach
            }
    }
}
                """.trimIndent()
        myFixture.configureByText(KotlinFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())

        doHighlighting
                .filter { it.forcedTextAttributes != null && it.text.contains("@") }
                .map { it.forcedTextAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                squigglyLevel(3),
                                squigglyLevel(4),
                                squigglyLevel(3),
                                squigglyLevel(2)
                        )
                )
    }

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
                .filter { it.forcedTextAttributes != null }
                .map { it.forcedTextAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),
                                roundLevel(0),
                                squigglyLevel(0),
                                squigglyLevel(1),
                                squigglyLevel(1),
                                squigglyLevel(0)
                        )
                )
    }
}