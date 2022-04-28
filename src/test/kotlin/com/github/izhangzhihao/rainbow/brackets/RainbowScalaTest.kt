package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.plugins.scala.ScalaFileType

class RainbowScalaTest : LightJavaCodeInsightFixtureTestCase() {

    override fun tearDown() {
        super.tearDown()
        val rainbowSettings = RainbowSettings.instance
        rainbowSettings.isRainbowEnabled = true
        rainbowSettings.isEnableRainbowAngleBrackets = true
        rainbowSettings.isEnableRainbowRoundBrackets = true
        rainbowSettings.isEnableRainbowSquareBrackets = true
        rainbowSettings.isEnableRainbowSquigglyBrackets = true
        rainbowSettings.isDoNOTRainbowifyBracketsWithoutContent = false
    }

    fun testRainbowForScala() {
        @Language("Scala") val code =
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
                .filter { it?.forcedTextAttributesKey != null }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
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

    fun testDisableRainbowSquareBracketsForScala() {
        val rainbowSettings = RainbowSettings.instance
        rainbowSettings.isEnableRainbowSquareBrackets = false
        @Language("Scala") val code =
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
                .filter { it?.forcedTextAttributesKey != null }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(

                                roundLevel(0),
                                roundLevel(0),

                                squigglyLevel(0),
                                squigglyLevel(1),
                                roundLevel(0),
                                roundLevel(0),
                                squigglyLevel(1),
                                squigglyLevel(0)
                        )
                )
    }
}