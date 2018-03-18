package com.github.izhangzhihao.rainbow.brackets

import com.intellij.notification.*
import com.intellij.openapi.project.Project

fun show(project: Project, title: String, content: String,
         displayId: String, type: NotificationType,
         listener: NotificationListener) {
    val group = NotificationGroup(
            displayId,
            NotificationDisplayType.STICKY_BALLOON,
            true
    )
    val notification = group.createNotification(title, content, type, listener)
    Notifications.Bus.notify(notification, project)
}