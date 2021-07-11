package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.color.Luminosity
import com.github.izhangzhihao.rainbow.brackets.color.fromString
import org.json.JSONObject
import java.awt.Color

fun randomColor(options: String): Color {
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