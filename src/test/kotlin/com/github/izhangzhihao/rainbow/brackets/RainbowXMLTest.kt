package com.github.izhangzhihao.rainbow.brackets

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.matchers.shouldBe

class RainbowXMLTest : LightCodeInsightFixtureTestCase() {
    fun testRainbowForXML() {
        val code =
                """
<idea-plugin>
    <name>Rainbow Brackets</name>
    <description>
        <p>Supported languages:</p>
        <p>Java, Scala, Clojure, Kotlin, Python, Haskell, Agda, Rust, JavaScript, TypeScript, Erlang, Go, Groovy, Ruby, Elixir, ObjectiveC, PHP, C#, HTML, XML, SQL, Apex language ...</p>
        <br/>
    </description>
</idea-plugin>
                """.trimIndent()
        myFixture.configureByText(XmlFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting
                .map { it.forcedTextAttributes.foregroundColor.rgb }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                angleLevel(0),
                                angleLevel(0),

                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(1),

                                angleLevel(1),
                                angleLevel(1),

                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),

                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),

                                angleLevel(2),
                                angleLevel(2),

                                angleLevel(1),
                                angleLevel(1),

                                angleLevel(0),
                                angleLevel(0)
                        )
                )
    }
}