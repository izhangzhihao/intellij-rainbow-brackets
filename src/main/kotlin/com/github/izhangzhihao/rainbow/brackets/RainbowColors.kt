package com.github.izhangzhihao.rainbow.brackets

import com.intellij.ui.JBColor
import java.awt.Color


object RainbowColors {

    val roundBracketsColor: Array<Color> = arrayOf(
            JBColor(0xE66A01, 0xE6B422),
            JBColor(0x109321, 0x00A960),
            JBColor(0xFF3E5D, 0xFC7482),
            JBColor(0x9C27B0, 0x9C27B0),
            JBColor(0x006BE7, 0x2196F3)
    )

    val squareBracketsColor: Array<Color> = arrayOf(
            JBColor(0x0B9087, 0x33CCFF),
            JBColor(0x6444E6, 0x8080FF),
            JBColor(0x00599B, 0x0073A8)
    )

    val squigglyBracketsColor: Array<Color> = arrayOf(
            JBColor(0x0057D2, 0x1976D2),
            JBColor(0x509629, 0xCDDC39),
            JBColor(0xA526AA, 0x965096),
            JBColor(0xFF6D27, 0xFF9863)
    )

    val angleBracketsColor = roundBracketsColor
}