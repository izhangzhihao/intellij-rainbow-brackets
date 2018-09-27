package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.colors.impl.AbstractColorsScheme
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.matchers.shouldBe
import java.awt.Color

class MigrateSettingsTest : LightCodeInsightFixtureTestCase() {

    private fun EditorColorsScheme.read(rainbowName: String): List<Color> {
        return RainbowHighlighter.getRainbowAttributesKeys(rainbowName).map { getAttributes(it).foregroundColor }
    }

    fun testMigrateFromCustomDarkColorSchema() {
        val round = arrayOf(
                "0x11111",
                "0x22222",
                "0x33333",
                "0x44444",
                "0x55555"
        )
        val square = arrayOf(
                "0x11111",
                "0x22222",
                "0x33333"
        )
        val squiggly = arrayOf(
                "0x44444",
                "0x55555",
                "0x66666"
        )
        val settings = RainbowSettings.instance
        settings.darkRoundBracketsColors = round
        settings.darkSquareBracketsColors = square
        settings.darkSquigglyBracketsColors = squiggly
        settings.darkAngleBracketsColor = round

        migrateRainbowColors(settings)

        val schemeGroup = EditorColorsManager.getInstance()
                .allSchemes
                .mapNotNull { scheme -> (scheme as? AbstractColorsScheme)?.takeUnless { it.isReadOnly } }
                .groupBy { it.parentScheme?.name }

        val darculaSchema = schemeGroup[DARCULA_SCHEME_NAME]?.get(0)
        darculaSchema?.read(RainbowHighlighter.NAME_ROUND_BRACKETS).shouldBe(round.map { color(it) })
        darculaSchema?.read(RainbowHighlighter.NAME_SQUIGGLY_BRACKETS).shouldBe(squiggly.map { color(it) })
        darculaSchema?.read(RainbowHighlighter.NAME_ANGLE_BRACKETS).shouldBe(round.map { color(it) })
        darculaSchema?.read(RainbowHighlighter.NAME_SQUARE_BRACKETS).shouldBe(square.map { color(it) })
    }

    fun testMigrateFromCustomSomeColorSchema() {
        val round = arrayOf(
                "0x11111",
                "0x22222",
                "0x33333",
                "0x44444",
                "0x55555"
        )
        val square = arrayOf(
                "0x11111",
                "0x22222",
                "0x33333"
        )
        val settings = RainbowSettings.instance
        settings.darkRoundBracketsColors = round
        settings.lightSquareBracketsColors = square

        migrateRainbowColors(settings)

        val schemeGroup = EditorColorsManager.getInstance()
                .allSchemes
                .mapNotNull { scheme -> (scheme as? AbstractColorsScheme)?.takeUnless { it.isReadOnly } }
                .groupBy { it.parentScheme?.name }

        val darculaSchema = schemeGroup[DARCULA_SCHEME_NAME]?.get(0)
        darculaSchema?.read(RainbowHighlighter.NAME_ROUND_BRACKETS).shouldBe(round.map { color(it) })

        val defaultSchema = schemeGroup[DEFAULT_SCHEME_NAME]?.get(0)
        defaultSchema?.read(RainbowHighlighter.NAME_SQUARE_BRACKETS).shouldBe(square.map { color(it) })
    }
}