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

    fun testRainbowArrowForKotlin() {
        val code =
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
        val code =
                """
class AA {
    fun aa() {
        return@aa
    }

    inner class BB {
        fun BB.bb(): Any = TODO()
        fun cc() {
            this@AA.aa()
            this@BB.bb()
            arrayOf(1, 2, 3).forEach {
                it.let dd@{
                    if (it > 0) a@ {
                        return@dd
                    }
                }
                return@forEach
            };
            e@ {
                f@{}
                g@ { h@{ return@e } }()
                Unit
            }()
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
                                squigglyLevel(1),
                                squigglyLevel(0),
                                squigglyLevel(1),
                                squigglyLevel(1),
                                squigglyLevel(2),
                                squigglyLevel(1),
                                squigglyLevel(0),
                                squigglyLevel(0),
                                squigglyLevel(1),
                                squigglyLevel(1),
                                squigglyLevel(2),
                                squigglyLevel(0)
                        )
                )
    }

    fun testKotlinFunctionLiteralBracesAndArrow() {
        val code =
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