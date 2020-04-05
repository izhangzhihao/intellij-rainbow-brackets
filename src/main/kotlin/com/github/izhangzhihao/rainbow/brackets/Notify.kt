package com.github.izhangzhihao.rainbow.brackets

import com.intellij.notification.*
import com.intellij.notification.impl.NotificationsManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.BalloonLayoutData
import com.intellij.ui.awt.RelativePoint
import java.awt.Point

fun createNotification(title: String, content: String, displayId: String,
                       type: NotificationType, listener: NotificationListener): Notification {
    val group = NotificationGroup(
            displayId,
            NotificationDisplayType.STICKY_BALLOON,
            true
    )
    return group.createNotification(title, content, type, listener)
}

fun showFullNotification(project: Project, notification: Notification) {
    val frame = WindowManager.getInstance().getIdeFrame(project)
    if (frame == null) {
        notification.notify(project)
        return
    }
    val bounds = frame.component.bounds
    val target = RelativePoint(frame.component, Point(bounds.x + bounds.width, 20))

    try {
        val balloon = NotificationsManagerImpl.createBalloon(frame,
                notification,
                true, // showCallout
                false, // hideOnClickOutside
                BalloonLayoutData.fullContent()) { }
        balloon.show(target, Balloon.Position.atLeft)
    } catch (e: Exception) {
        notification.notify(project)
    }
}