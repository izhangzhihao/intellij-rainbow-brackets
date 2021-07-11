package com.github.izhangzhihao.rainbow.brackets

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.izhangzhihao.rainbow.brackets.color.Luminosity
import com.github.izhangzhihao.rainbow.brackets.color.fromString
import java.awt.Color

val mapper: ObjectMapper by lazy { jacksonObjectMapper() }

fun randomColor(options: String): Color {
    val ops: Map<String, String> = mapper.readValue(options)
    return com.github.izhangzhihao.rainbow.brackets.color.randomColor(
        fromString(ops.getOrDefault("hue", "random")),
        Luminosity.valueOf(ops.getOrDefault("luminosity", "random"))
    )
}