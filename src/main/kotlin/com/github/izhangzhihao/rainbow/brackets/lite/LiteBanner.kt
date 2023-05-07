package com.github.izhangzhihao.rainbow.brackets.lite

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.plugins.PluginManagerCore.isPluginInstalled
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationEx
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import com.intellij.ui.HyperlinkLabel
import com.intellij.icons.AllIcons

class LiteBanner : EditorNotifications.Provider<EditorNotificationPanel>() {
    override fun getKey(): Key<EditorNotificationPanel> = KEY

    override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor, project: Project): EditorNotificationPanel? {
        val pluginId = PluginId.getId("izhangzhihao.rainbow.brackets")
        val liteId = PluginId.getId("izhangzhihao.rainbow.brackets.lite")
        val isInstalled = isPluginInstalled(pluginId)
        if (isInstalled) {
            if (PluginManagerCore.isDisabled(pluginId) || PluginManagerCore.isDisabled(liteId)) {
                return null
            }
            return EditorNotificationPanel().apply {
                text("You could not use Rainbow Brackets and Lite version at the same time.")
                icon(AllIcons.General.Tip)

                createComponentActionLabel("Disable Rainbow Brackets Lite") {
                    PluginManagerCore.disablePlugin(liteId)
                    EditorNotifications.getInstance(project).updateAllNotifications()
                    val app = ApplicationManager.getApplication() as ApplicationEx
                    app.restart(true)
                }

                createComponentActionLabel("Disable Rainbow Brackets") {
                    PluginManagerCore.disablePlugin(pluginId)
                    EditorNotifications.getInstance(project).updateAllNotifications()
                    val app = ApplicationManager.getApplication() as ApplicationEx
                    app.restart(true)
                }
            }
        } else {
            return null
        }
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("LiteBanner")
        fun EditorNotificationPanel.createComponentActionLabel(labelText: String, callback: (HyperlinkLabel) -> Unit) {
            val label: Ref<HyperlinkLabel> = Ref.create()
            label.set(createActionLabel(labelText) {
                callback(label.get())
            })
        }
    }
}