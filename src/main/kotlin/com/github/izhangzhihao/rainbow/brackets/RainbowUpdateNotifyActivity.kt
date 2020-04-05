package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.NotificationListener.UrlOpeningListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import org.intellij.lang.annotations.Language

class RainbowUpdateNotifyActivity : StartupActivity {
    var updated: Boolean = false

    override fun runActivity(project: Project) {
        val settings = RainbowSettings.instance
        updated = getPlugin()?.version != settings.version
        if (updated) {
            settings.version = getPlugin()!!.version
            showUpdate(project)
            updated = false
        }
    }

    private fun showUpdate(project: Project) {
        val notification = createNotification(
                "Rainbow Brackets updated to $version",
                updateContent,
                channel,
                NotificationType.INFORMATION,
                UrlOpeningListener(false)
        )
        showFullNotification(project, notification)
    }

    companion object {
        private val version = RainbowSettings.instance.version
        private var channel = "izhangzhihao.rainbow.brackets"

        @Language("HTML")
        private val updateContent = """
    <br/>
    🌈Thank you for downloading <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets">Rainbow Brackets</a></b>!<br>
    🎉Sponsored by <a href="https://codestream.com/?utm_source=jbmarket&utm_medium=banner&utm_campaign=jbrainbowbrackets">CodeStream</a>.<br>
    👍If you find this plugin helpful, <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#support-us">please support us!</a>.</b><br>
    <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#support-us">Donate</a></b> by <b><a href="https://opencollective.com/intellij-rainbow-brackets">OpenCollective</a></b> Or AliPay/WeChatPay to <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#sponsors">become a sponsor</a>!.</b><br>
    📝Check out <b><a href="https://izhangzhihao.github.io/rainbow-brackets-document/">the document</a></b> for all features of this plugin.<br>
    🐛If you run into any problem, <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues">feel free to raise a issue</a>.</b><br>
    🆕See <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/releases/tag/$version">changelog</a></b> for more details about this update.<br>
    👉Want to customize your own color scheme of Rainbow Brackets? Edit it under
    <b>Settings > Editor > Color Scheme > Rainbow Brackets</b><br>
    👉Other additional feature under
    <b>Settings > Other Settings > Rainbow Brackets</b><br/>
    Enjoy your colorful code🌈.
    """

        fun getPlugin(): IdeaPluginDescriptor? = PluginManagerCore.getPlugin(
                PluginId.getId("izhangzhihao.rainbow.brackets"))

    }
}