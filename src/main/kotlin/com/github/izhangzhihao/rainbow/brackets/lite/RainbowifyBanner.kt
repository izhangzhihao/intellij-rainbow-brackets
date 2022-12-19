package com.github.izhangzhihao.rainbow.brackets.lite

import com.github.izhangzhihao.rainbow.brackets.lite.settings.RainbowConfigurable
import com.github.izhangzhihao.rainbow.brackets.lite.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.lite.util.toPsiFile
import com.github.izhangzhihao.rainbow.brackets.lite.visitor.RainbowHighlightVisitor.Companion.checkForBigFile
import com.intellij.icons.AllIcons
import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import com.intellij.ui.HyperlinkLabel

class RainbowifyBanner : EditorNotifications.Provider<EditorNotificationPanel>() {
    override fun getKey(): Key<EditorNotificationPanel> = KEY

    override fun createNotificationPanel(
        file: VirtualFile,
        fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {

        val psiFile = file.toPsiFile(project)
        if (psiFile != null && !checkForBigFile(psiFile)) {
            if (RainbowSettings.instance.suppressBigFileCheck) return null
            return EditorNotificationPanel().apply {
                text("Rainbowify is disabled for files > " + RainbowSettings.instance.bigFilesLinesThreshold + " lines")
                icon(AllIcons.General.InspectionsEye)
                createComponentActionLabel("got it, don't show again") {
                    RainbowSettings.instance.suppressBigFileCheck = true
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }

                createComponentActionLabel("open settings") {
                    ShowSettingsUtilImpl.showSettingsDialog(project, RainbowConfigurable.ID, "")
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }
            }
        }

        return null
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("RainbowifyBanner")

        fun EditorNotificationPanel.createComponentActionLabel(labelText: String, callback: (HyperlinkLabel) -> Unit) {
            val label: Ref<HyperlinkLabel> = Ref.create()
            label.set(createActionLabel(labelText) {
                callback(label.get())
            })
        }
    }
}
