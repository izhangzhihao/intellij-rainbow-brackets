package com.github.izhangzhihao.rainbow.brackets

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.matchers.shouldBe
import org.jetbrains.plugins.scala.ScalaFileType

class RainbowScalaTest : LightCodeInsightFixtureTestCase() {
    fun testRainbowForScala() {
        val code =
                """
import scala.annotation.tailrec

 @tailrec
  private def loop[V](key: String): V = {
    key match {
      case _ => loop[V](key)
    }
  }
                """.trimIndent()
        myFixture.configureByText(ScalaFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .filter { it?.forcedTextAttributes != null }
                .map { it.forcedTextAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                squareLevel(0),
                                squareLevel(0),

                                roundLevel(0),
                                roundLevel(0),

                                squigglyLevel(0),
                                squigglyLevel(1),
                                squareLevel(0),
                                squareLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                squigglyLevel(1),
                                squigglyLevel(0)
                        )
                )
    }
}