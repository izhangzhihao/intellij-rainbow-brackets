package com.github.izhangzhihao.rainbow.brackets.color


sealed class Hue
object RandomHue : Hue()
data class NumberHue(val value: Int) : Hue()
data class ColorHue(val color: Color) : Hue()

fun Hue.getHueRange(): Pair<Int, Int> {
    return when (this) {
        is ColorHue -> color.hueRange
        is NumberHue -> if (value in 1..359) Pair(value, value) else Pair(0, 360)
        RandomHue -> Pair(0, 360)
    }
}

fun fromString(str: String): Hue {
    return when {
        str == "random" -> RandomHue
        str.startsWith("#") -> NumberHue(Integer.parseInt(str.replaceFirst("#", ""), 16))
        else -> ColorHue(Color.valueOf(str))
    }
}
