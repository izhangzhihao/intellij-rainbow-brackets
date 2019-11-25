package com.github.izhangzhihao.rainbow.brackets.settings

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe

class RainbowConfigurableTest : LightCodeInsightFixtureTestCase() {

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

    fun testSettings() {
        val rainbowConfigurable = RainbowConfigurable()
        rainbowConfigurable.isModified shouldBe false
        rainbowConfigurable.displayName shouldBe "Rainbow Brackets"
        rainbowConfigurable.createComponent()
        rainbowConfigurable.settingsForm.shouldNotBe(null)

        rainbowConfigurable.settingsForm!!.isRainbowEnabled() shouldBe true
        rainbowConfigurable.settingsForm!!.isRainbowAngleBracketsEnabled() shouldBe true
        rainbowConfigurable.settingsForm!!.isRainbowRoundBracketsEnabled() shouldBe true
        rainbowConfigurable.settingsForm!!.isRainbowSquareBracketsEnabled() shouldBe true
        rainbowConfigurable.settingsForm!!.isRainbowSquigglyBracketsEnabled() shouldBe true
        rainbowConfigurable.settingsForm!!.isShowRainbowIndentGuides() shouldBe true
        rainbowConfigurable.settingsForm!!.isDoNOTRainbowifyBracketsWithoutContent() shouldBe false

        rainbowConfigurable.disposeUIResources()
        rainbowConfigurable.settingsForm.shouldBe(null)
    }

    fun testUpdateSettings() {

        val rainbowSettings = RainbowSettings.instance
        rainbowSettings.isRainbowEnabled = false
        rainbowSettings.isEnableRainbowAngleBrackets = false
        rainbowSettings.isEnableRainbowRoundBrackets = false
        rainbowSettings.isEnableRainbowSquareBrackets = false
        rainbowSettings.isEnableRainbowSquigglyBrackets = false
        rainbowSettings.isDoNOTRainbowifyBracketsWithoutContent = true

        val rainbowConfigurable = RainbowConfigurable()
        rainbowConfigurable.createComponent()
        rainbowConfigurable.apply()

        rainbowConfigurable.settingsForm!!.isRainbowEnabled() shouldBe false
        rainbowConfigurable.settingsForm!!.isRainbowAngleBracketsEnabled() shouldBe false
        rainbowConfigurable.settingsForm!!.isRainbowRoundBracketsEnabled() shouldBe false
        rainbowConfigurable.settingsForm!!.isRainbowSquareBracketsEnabled() shouldBe false
        rainbowConfigurable.settingsForm!!.isRainbowSquigglyBracketsEnabled() shouldBe false
        rainbowConfigurable.settingsForm!!.isDoNOTRainbowifyBracketsWithoutContent() shouldBe true

        rainbowConfigurable.disposeUIResources()
        rainbowConfigurable.settingsForm.shouldBe(null)
    }
}