package com.github.izhangzhihao.rainbow.brackets.component

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseComponent
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.Disposer

class RainbowComponent : BaseComponent, EditorColorsListener {

    var updated: Boolean = false

    private val disposable = Disposer.newDisposable(javaClass.name)

    override fun initComponent() {
        val settings = RainbowSettings.instance
        updated = getPlugin()?.version != settings.version
        if (updated) {
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


        val isIntelliJHaskellEnabled: Boolean by lazy {
            PluginManager.getPlugin(
                    PluginId.getId("intellij.haskell"))?.isEnabled ?: false
        }

        val isHaskForceEnabled: Boolean by lazy {
            PluginManager.getPlugin(
                    PluginId.getId("com.haskforce"))?.isEnabled ?: false
        }
    }
}