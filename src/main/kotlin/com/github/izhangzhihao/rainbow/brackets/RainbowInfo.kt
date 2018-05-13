package com.github.izhangzhihao.rainbow.brackets

import com.intellij.openapi.util.Key
import java.awt.Color

/**
 * RainbowInfo
 *
 * Created by Yii.Guxing on 2018/05/12
 */
data class RainbowInfo(var level: Int, var color: Color, var startOffset: Int = -1, var endOffset: Int = -1) {

    fun isValid(): Boolean = startOffset in 0..(endOffset - 1)

    companion object {
        val KEY_RAINBOW: Key<RainbowInfo> = Key.create("RAINBOW_INFO")
    }
}