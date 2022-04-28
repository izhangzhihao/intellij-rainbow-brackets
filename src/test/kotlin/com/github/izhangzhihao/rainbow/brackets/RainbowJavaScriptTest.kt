package com.github.izhangzhihao.rainbow.brackets

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.lang.ecmascript6.JSXHarmonyFileType
import com.intellij.lang.javascript.JavaScriptFileType
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class RainbowJavaScriptTest : LightJavaCodeInsightFixtureTestCase() {

    fun testJavaScriptPluginEnabled() {
        assertTrue(PluginManagerCore.getPlugin(PluginId.getId("JavaScript"))?.isEnabled!!)
    }

    fun testIssue11() {
        @Language("JavaScript") val code = """
"use strict";

const _ = require('lodash') || false
const moment = require('moment')
                            """.trimIndent()

        myFixture.configureByText(JavaScriptFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0)
                        )
                )
    }

    fun testIssue12() {
        @Language("JavaScript") val code = """
"use strict";
console.log(a > b)
console.log(a == b)
                            """.trimIndent()
        myFixture.configureByText(JavaScriptFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0)
                        )
                )
    }

    fun testIssue21() {

        @Language("JavaScript") val code = "open (\$" + "{f})\n" + "open (\$" + "{f} )"

        myFixture.configureByText(JavaScriptFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0),
                                roundLevel(0)
                        )
                )
    }

    fun testIssue23() {
        @Language("JavaScript") val code = """
"use strict";

var a;

if ((a.field_detail && a.is) ||
    (a.field_detail && a.is) ||
    (a.field_detail && a.is) ||
    (a.field_detail && a.is) ||
    (a.field_detail && a.is) ||
    (a.field_detail && a.is) ||
    (a.field_detail && a.is)
) ;

                            """.trimIndent()

        myFixture.configureByText(JavaScriptFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(1),
                                roundLevel(0)
                        )
                )
    }

    fun testIssue38() {
        @Language("JavaScript") val code = """
const element = ( <div> <h1>Hello, world!</h1> </div> );
                            """.trimIndent()

        myFixture.configureByText(JSXHarmonyFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .filterNot { it == null }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),

                                angleLevel(0),
                                angleLevel(0),
                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(0),
                                angleLevel(0),

                                roundLevel(0)
                        )
                )
    }

    fun `for somehow, it just don't work "testIssue39"`() {
        @Language("JavaScript") val code = """
    const html = '<div><div><div>Hello</div></div></div>'
                                """.trimIndent()

        myFixture.configureByText(JavaScriptFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .filterNot { it == null }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                angleLevel(0),
                                angleLevel(0),
                                angleLevel(1),
                                angleLevel(1),
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

    fun testIssue31() {
        @Language("JavaScript") val code = """
    "use strict";
    const f = () => {}
    const a = [1,2,3]
    const s = `<ololo>`
                                """.trimIndent()

        myFixture.configureByText(TypeScriptFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .filterNot { it == null }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),
                                roundLevel(0),
                                squigglyLevel(0),
                                squigglyLevel(0),
                                squareLevel(0),
                                squareLevel(0)
                                //, angleLevel(0)
                                //, angleLevel(0)
                        )
                )
    }

    fun testIssue427() {
        @Language("TypeScript") val code = """let example: Array<Map<string,string>>;""".trimIndent()

        myFixture.configureByText(TypeScriptFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .filterNot { it == null }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                angleLevel(0),
                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(0)
                        )
                )
    }

}