package com.github.izhangzhihao.rainbow.brackets.lite

import com.github.izhangzhihao.rainbow.brackets.lite.settings.RainbowSettings
import com.intellij.ide.plugins.PluginManagerCore.isPluginInstalled
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class RainbowCheckActivity : StartupActivity {

    override fun runActivity(project: Project) {
        val pluginId = PluginId.getId("izhangzhihao.rainbow.brackets")
        val isInstalled = isPluginInstalled(pluginId)
        if (isInstalled) {
            RainbowSettings.instance.isRainbowEnabled = false
        }
    }
}
