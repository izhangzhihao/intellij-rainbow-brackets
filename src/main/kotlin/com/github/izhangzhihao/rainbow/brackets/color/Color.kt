package com.github.izhangzhihao.rainbow.brackets.color

import kotlin.math.floor

enum class Color(val hueRange: Pair<Int, Int>, val lowerBounds: List<Pair<Int, Int>>) {
    monochrome(
        Pair(-1, -1),
        listOf(Pair(0, 0), Pair(100, 0))
    ),
    red(
        Pair(-26, 18),
        listOf(
            Pair(20, 100),
            Pair(30, 92),
            Pair(40, 89),
            Pair(50, 85),
            Pair(60, 78),
            Pair(70, 70),
            Pair(80, 60),
            Pair(90, 55),
            Pair(100, 50)
        )
    ),
    orange(
        Pair(18, 46),
        listOf(Pair(20, 100), Pair(30, 93), Pair(40, 88), Pair(50, 86), Pair(60, 85), Pair(70, 70), Pair(100, 70))
    ),
    yellow(
        Pair(46, 62),
        listOf(
            Pair(25, 100),
            Pair(40, 94),
            Pair(50, 89),
            Pair(60, 86),
            Pair(70, 84),
            Pair(80, 82),
            Pair(90, 80),
            Pair(100, 75)
        )
    ),
    green(
        Pair(62, 178),
        listOf(
            Pair(30, 100),
            Pair(40, 90),
            Pair(50, 85),
            Pair(60, 81),
            Pair(70, 74),
            Pair(80, 64),
            Pair(90, 50),
            Pair(100, 40)
        )
    ),
    blue(
        Pair(178, 257),
        listOf(
            Pair(20, 100),
            Pair(30, 86),
            Pair(40, 80),
            Pair(50, 74),
            Pair(60, 60),
            Pair(70, 52),
            Pair(80, 44),
            Pair(90, 39),
            Pair(100, 35)
        )
    ),
    purple(
        Pair(257, 282),
        listOf(
            Pair(20, 100),
            Pair(30, 87),
            Pair(40, 79),
            Pair(50, 70),
            Pair(60, 65),
            Pair(70, 59),
            Pair(80, 52),
            Pair(90, 45),
            Pair(100, 42)
        )
    ),
    pink(
        Pair(282, 334),
        listOf(Pair(20, 100), Pair(30, 90), Pair(40, 86), Pair(60, 84), Pair(80, 80), Pair(90, 75), Pair(100, 73))
    )
}

fun Color.saturationRange(): Pair<Int, Int> {
    return Pair(lowerBounds.first().first, lowerBounds.last().first)
}

fun Color.brightnessRange(saturation: Int): Pair<Int, Int> {
    for (i in 0 until lowerBounds.size - 1) {
        val s1 = lowerBounds[i].first.toFloat()
        val v1 = lowerBounds[i].second.toFloat()

        val s2 = lowerBounds[i + 1].first.toFloat()
        val v2 = lowerBounds[i + 1].second.toFloat()

        if (saturation.toFloat() in s1..s2) {
            val m = (v2 - v1) / (s2 - s1)
            val b = v1 - m * s1
            val minBrightness = m * saturation + b
            return Pair(floor(minBrightness).toInt(), 100)
        }
    }
    return Pair(0, 100)
}