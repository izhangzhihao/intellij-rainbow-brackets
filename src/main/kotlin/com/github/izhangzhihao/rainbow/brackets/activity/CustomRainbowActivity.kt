package com.github.izhangzhihao.rainbow.brackets.activity

import com.github.izhangzhihao.rainbow.brackets.SettingsOpeningListener
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowColorsPageFactory
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.show
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class CustomRainbowActivity : StartupActivity {

    override fun runActivity(project: Project) {
        if (!RainbowSettings.instance.isCustomNotifyShowed) {
            notify(project)
        }
    }

    companion object {
        private var channel = "izhangzhihao.rainbow.brackets"
        private const val notifyContent = """
    Want to custom your own color scheme of Rainbow Brackets? Edit it under
    <a href="#">Settings > Editor > Color Scheme > Rainbow Brackets</a>
    """

        private fun notify(project: Project) {
            show(
                    "Want to custom your own rainbow colors?",
                    notifyContent,
                    channel + "_UPDATE",
                    NotificationType.INFORMATION,
                    SettingsOpeningListener(project, RainbowColorsPageFactory().displayName)
            )
        }
    }
}