package com.github.izhangzhihao.rainbow.brackets

import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.jetbrains.php.lang.PhpFileType
import io.kotest.matchers.shouldBe

class RainbowPHPTest : LightJavaCodeInsightFixtureTestCase() {
    fun testRainbowForPHP() {
        val code =
                """
<?php

function padZero(string data): string
{
    remainder = Binary::safeStrlen(data) % 8;
    if (remainder > 0) {
        data = str_repeat(self::NULL_BYTE, 8 - remainder);
    }

    return data;
}

?>
                """
        myFixture.configureByText(PhpFileType.INSTANCE, code)
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

                                squigglyLevel(0),

                                roundLevel(1),
                                roundLevel(1),

                                roundLevel(1),
                                roundLevel(1),

                                squigglyLevel(1),
                                roundLevel(2),
                                roundLevel(2),
                                squigglyLevel(1),

                                squigglyLevel(0)
                        )
                )
    }
}