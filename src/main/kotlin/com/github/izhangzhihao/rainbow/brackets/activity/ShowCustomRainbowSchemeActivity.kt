package com.github.izhangzhihao.rainbow.brackets.activity

import com.github.izhangzhihao.rainbow.brackets.SettingsOpeningListener
import com.github.izhangzhihao.rainbow.brackets.createNotification
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowColorsPageFactory
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class ShowCustomRainbowSchemeActivity : StartupActivity {

    override fun runActivity(project: Project) {
        if (!RainbowSettings.instance.isCustomNotifyShowed) {
            notify(project)
        }
    }

    companion object {
        private var channel = "izhangzhihao.rainbow.brackets"
        private const val notifyContent = """
    Want to customize your own color scheme of Rainbow Brackets? Edit it under
    <a href="#">Settings > Editor > Color Scheme > Rainbow Brackets</a>
    <br/>
    Check out <b><a href="https://izhangzhihao.github.io/rainbow-brackets-document/">the document</a></b> for all the features of this plugin.<br>
    """

        private fun notify(project: Project) {
            val notification = createNotification(
                    "Want to customize your own rainbow colors?",
                    notifyContent,
                    channel,
                    NotificationType.INFORMATION,
                    SettingsOpeningListener(project, RainbowColorsPageFactory().displayName)
            )
            Notifications.Bus.notify(notification)
            RainbowSettings.instance.isCustomNotifyShowed = true
        }
    }
}
