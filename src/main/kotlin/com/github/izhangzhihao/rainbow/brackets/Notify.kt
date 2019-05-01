package com.github.izhangzhihao.rainbow.brackets

import com.intellij.notification.*
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import javax.swing.event.HyperlinkEvent

fun show(title: String, content: String, displayId: String,
         type: NotificationType, listener: NotificationListener) {
    val group = NotificationGroup(
            displayId,
            NotificationDisplayType.STICKY_BALLOON,
            true
    )
    val notification = group.createNotification(title, content, type, listener)
    Notifications.Bus.notify(notification)
}

class SettingsOpeningListener(val project: Project, private val nameToSelect: String) : NotificationListener.Adapter() {
    override fun hyperlinkActivated(notifi: Notification, event: HyperlinkEvent) {
        if (!project.isDisposed) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, nameToSelect)
        }
    }
}