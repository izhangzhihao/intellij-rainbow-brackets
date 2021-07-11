package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.color.Luminosity
import com.github.izhangzhihao.rainbow.brackets.color.fromString
import org.json.JSONObject
import java.awt.Color

fun randomColor(options: String): String {
    val options = JSONObject(options)
    return com.github.izhangzhihao.rainbow.brackets.color.randomColor(
        fromString(options.getStringOrDefault("hue", "random")),
        Luminosity.valueOf(options.getStringOrDefault("luminosity", "random"))
    )
}

fun org.json.JSONObject.getStringOrDefault(key: String, default: String): String {
    return try {
        this.getString(key)
    } catch (e: Exception) {
        default
    }
}

fun fromRGBstr(str: String): Color {
    val split = str.trimStart('(').trimEnd(')').split(", ")
    return Color(split[0].toInt(), split[1].toInt(), split[2].toInt())
}