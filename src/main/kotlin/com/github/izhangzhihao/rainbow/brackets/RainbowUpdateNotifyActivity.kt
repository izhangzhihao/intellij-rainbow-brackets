package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.plugins.PluginManagerCore.isPluginInstalled
import com.intellij.ide.startup.StartupActionScriptManager
import com.intellij.ide.startup.StartupActionScriptManager.DeleteCommand
import com.intellij.notification.NotificationListener.UrlOpeningListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


class RainbowUpdateNotifyActivity : StartupActivity {

    override fun runActivity(project: Project) {
        removeIfInstalled()
        val settings = RainbowSettings.instance
        if (getPlugin()?.version != settings.version) {
            settings.version = getPlugin()!!.version
            if (settings.showNotificationOnUpdate) {
                showUpdate(project)
            }
        }
    }

    private fun removeIfInstalled() {
        val pluginId = PluginId.getId("com.github.jadepeng.rainbowfart")
        val isInstalled = isPluginInstalled(pluginId)
        if (isInstalled) {
            val pluginDescriptor = PluginManagerCore.getPlugin(pluginId)
            if (pluginDescriptor != null) {
                //disablePlugin(pluginId)
                StartupActionScriptManager.addActionCommand(DeleteCommand(pluginDescriptor.pluginPath.toFile()))
            }
        }
    }

    companion object {
        private const val pluginId = "izhangzhihao.rainbow.brackets"


        private val updateContent: String by lazy {
            //language=HTML
            """
    <br/>
    🌈Thank you for downloading <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets">Rainbow Brackets</a></b>!<br>
    🎉Sponsored by <a href="https://www.codota.com/?utm_source=jbmarket&utm_medium=banner&utm_campaign=jbrainbowbrackets">Codota</a> and <a href="https://codestream.com/?utm_source=jbmarket&utm_medium=banner&utm_campaign=jbrainbowbrackets">CodeStream</a>.<br>
    👍If you find this plugin helpful, <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#support-us">please support us!</a>.</b><br>
    <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#support-us">Donate</a></b> by <b><a href="https://opencollective.com/intellij-rainbow-brackets">OpenCollective</a></b> Or AliPay/WeChatPay to <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets#sponsors">become a sponsor</a>!.</b><br>
    📝Check out <b><a href="https://izhangzhihao.github.io/rainbow-brackets-document/">the document</a></b> for all features of this plugin.<br>
    🐛If you run into any problem, <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues">feel free to raise a issue</a>.</b><br>
    🆕See <b><a href="${changelog()}">changelog</a></b> for more details about this update.<br>
    👉Want to customize your own color scheme of Rainbow Brackets? Edit it under
    <b>Settings > Editor > Color Scheme > Rainbow Brackets</b><br>
    👉Tired of the bundled colors? Try out the new color generator!
    <b>Settings > Other Settings > Rainbow Brackets > Use color generator</b><br>
    👉Other additional features under
    <b>Settings > Other Settings > Rainbow Brackets</b><br/>
    Enjoy your colorful code🌈.
    """
        }

        private fun changelog(): String {
            val plugin = getPlugin()
            return if (plugin == null) {
                """https://github.com/izhangzhihao/intellij-rainbow-brackets/releases"""
            } else {
                """https://github.com/izhangzhihao/intellij-rainbow-brackets/releases/tag/${plugin.version}"""
            }

        }

        fun getPlugin(): IdeaPluginDescriptor? = PluginManagerCore.getPlugin(PluginId.getId(pluginId))

        private fun updateMsg(): String {
            val plugin = getPlugin()
            return if (plugin == null) {
                "Rainbow Brackets installed."
            } else {
                "Rainbow Brackets updated to ${plugin.version}"
            }
        }

        private fun showUpdate(project: Project) {
            val notification = createNotification(
                    updateMsg(),
                    updateContent,
                    pluginId,
                    NotificationType.INFORMATION,
                    UrlOpeningListener(false)
            )
            showFullNotification(project, notification)
        }
    }
}