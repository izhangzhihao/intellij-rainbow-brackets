package com.github.izhangzhihao.rainbow.brackets.component

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter
import com.github.izhangzhihao.rainbow.brackets.migrateRainbowColors
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.Disposer

class RainbowComponent : ApplicationComponent, EditorColorsListener {

    var updated: Boolean = false

    private val disposable = Disposer.newDisposable(javaClass.name)

    override fun initComponent() {
        val settings = RainbowSettings.instance
        updated = getPlugin()?.version != settings.version
        if (updated) {
            migrateRainbowColors(settings)
            settings.version = getPlugin()!!.version
        }

        RainbowHighlighter.fixHighlighting()
        ApplicationManager.getApplication()
                .messageBus
                .connect(disposable)
                .subscribe<EditorColorsListener>(EditorColorsManager.TOPIC, this)
    }

    override fun globalSchemeChange(scheme: EditorColorsScheme?) {
        scheme?.let { RainbowHighlighter.fixHighlighting(it) }
    }

    override fun disposeComponent() {
        Disposer.dispose(disposable)
    }

    companion object {
        val instance: RainbowComponent
            get() = ApplicationManager.getApplication().getComponent(RainbowComponent::class.java)

        private fun getPlugin(): IdeaPluginDescriptor? = PluginManager.getPlugin(
                PluginId.getId("izhangzhihao.rainbow.brackets"))

    }
}