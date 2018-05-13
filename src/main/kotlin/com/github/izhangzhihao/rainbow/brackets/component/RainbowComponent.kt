package com.github.izhangzhihao.rainbow.brackets.component

import com.github.izhangzhihao.rainbow.brackets.CtrlHandler
import com.github.izhangzhihao.rainbow.brackets.EditorEventActionManager
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.extensions.PluginId

class RainbowComponent : ApplicationComponent {
    var updated: Boolean = false

    override fun initComponent() {
        EditorEventActionManager.instance.apply {
            registerEditorEventListener(CtrlHandler())
            install()
        }

        updated = getPlugin()?.version != RainbowSettings.instance.version
        if (updated) {
            RainbowSettings.instance.version = getPlugin()?.version
        }
        if (!RainbowSettings.instance.isRainbowifyHTMLInsideJS) {
            EditorColorsManager
                    .getInstance()
                    .globalScheme
                    .setAttributes(createTextAttributesKey("HTML_CODE"), TextAttributes())
            RainbowSettings.instance.isRainbowifyHTMLInsideJS = true
        }
    }

    override fun disposeComponent() {
        EditorEventActionManager.instance.uninstall()
        super.disposeComponent()
    }

    companion object {
        val instance: RainbowComponent
            get() = ApplicationManager.getApplication().getComponent(RainbowComponent::class.java)

        private fun getPlugin(): IdeaPluginDescriptor? = PluginManager.getPlugin(
                PluginId.getId("izhangzhihao.rainbow.brackets"))
    }
}