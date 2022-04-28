package com.github.izhangzhihao.rainbow.brackets

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.jetbrains.lang.dart.DartFileType
import io.kotest.matchers.shouldBe

class RainbowDartTest : LightJavaCodeInsightFixtureTestCase() {
    fun testRainbowForDart() {
        val code =
                """
void fn(List<int> a) => print(a);

void main() {
  var list = <int>[];
  list.add(1);
  list.add(2);
  fn(list);
}
                """.trimIndent()
        myFixture.configureByText(DartFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),
                                angleLevel(0),
                                angleLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                squigglyLevel(0),
                                angleLevel(0),
                                angleLevel(0),
                                squareLevel(0),
                                squareLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                squigglyLevel(0)
                        )
                )
    }
}