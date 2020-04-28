package com.github.izhangzhihao.rainbow.brackets

import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.Invocable
import javax.script.ScriptEngine

object RandomColorGenerator {
    private val engine: ScriptEngine by lazy { NashornScriptEngineFactory().getScriptEngine() }
    private val invocable: Invocable by lazy {
        engine.eval(javaClass.classLoader.getResource("randomColor.js").readText())
        engine as Invocable
    }

    fun randomColor(options: String): String {
        engine.put("paras", options)
        val obj = engine.eval("JSON.parse(paras)") as JSObject
        return invocable.invokeFunction("randomColor", obj).toString()
    }
}