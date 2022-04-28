package com.github.izhangzhihao.rainbow.brackets

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.plugins.groovy.GroovyFileType

class RainbowGroovyTest : LightJavaCodeInsightFixtureTestCase() {

    fun testRainbowForGroovy() {
        @Language("Groovy") val code =
                """
Map<String, Map<String, String>> convertObjectsToMapProperties(Map<String, Object> body) {
    return body.collectEntries {
        Map.Entry<String, Object> entry ->
            [entry.key: entry.value]
    } as Map<String, Map<String, String>>
}
                """.trimIndent()
        myFixture.configureByText(GroovyFileType.GROOVY_FILE_TYPE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                angleLevel(0),
                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(0),

                                roundLevel(0),
                                angleLevel(0),
                                angleLevel(0),
                                roundLevel(0),

                                squigglyLevel(0),
                                squigglyLevel(1),
                                angleLevel(0),
                                angleLevel(0),

                                squareLevel(0),

                                squareLevel(0),

                                squigglyLevel(1),

                                angleLevel(0),
                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(0),

                                squigglyLevel(0)
                        )
                )
    }
}