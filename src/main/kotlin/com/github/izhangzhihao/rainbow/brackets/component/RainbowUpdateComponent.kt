package com.github.izhangzhihao.rainbow.brackets.component

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.show
import com.intellij.notification.*
import com.intellij.openapi.components.AbstractProjectComponent
import com.intellij.openapi.project.Project

class RainbowUpdateComponent(project: Project) : AbstractProjectComponent(project) {

    private val applicationComponent = RainbowComponent.instance

    override fun projectOpened() {
        if (applicationComponent.updated) {
            showUpdate(myProject)
            applicationComponent.updated = false
        }
    }

    companion object {
        private val version = RainbowSettings.instance.version
        private var channel = "izhangzhihao.rainbow.brackets"
        private val updateContent = """
    <br/>
    Thank you for downloading <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets">Rainbow Brackets</a></b>!<br>
    If you find this plugin helpful, <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets">please give me a star on github</a>.</b>
    If you run into any issue, <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/issues">feel free to raise a issue</a>.</b><br/><br/>
    See <b><a href="https://github.com/izhangzhihao/intellij-rainbow-brackets/releases/tag/$version">changelog</a></b> for more details about this update.<br/><br/>
    Enjoy your colorful code.
    """

        private fun showUpdate(project: Project) {
            show(
                    project,
                    "Rainbow Brackets updated to $version",
                    updateContent,
                    channel + "_UPDATE",
                    NotificationType.INFORMATION,
                    NotificationListener.URL_OPENING_LISTENER
            )
        }
    }
}