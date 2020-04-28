package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import org.intellij.lang.annotations.Language

class RandomColorGenTest : LightJavaCodeInsightFixtureTestCase() {

    fun testRandomColorGenWorks() {
        val rainbowSettings = RainbowSettings.instance
        rainbowSettings.isRainbowEnabled = true
        rainbowSettings.useColorGenerator = true

        @Language("Java") val code =
                """
public class Test<T> {
    public Boolean test() {
        return "true".equals("false") || "false".equals("true".concat("false".length() + "*"));
    }
}
                """.trimIndent()
        myFixture.configureByText(JavaFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        val highlightSize = doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .filter { it.forcedTextAttributes.foregroundColor != null }
                .map { it.forcedTextAttributes.foregroundColor }
                .toTypedArray()
                .size
        assert(highlightSize == 16)
    }

}
