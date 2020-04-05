package com.github.izhangzhihao.rainbow.brackets.activity

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.Disposer

class RainbowStartupActivity : StartupActivity, EditorColorsListener {

    private val disposable = Disposer.newDisposable(javaClass.name)

    override fun runActivity(project: Project) {
        RainbowHighlighter.fixHighlighting()
        ApplicationManager.getApplication()
                .messageBus
                .connect(disposable)
                .subscribe(EditorColorsManager.TOPIC, this)

    }

    override fun globalSchemeChange(scheme: EditorColorsScheme?) {
        scheme?.let { RainbowHighlighter.fixHighlighting(it) }
    }

    companion object {
        val instance: RainbowStartupActivity
            get() = ApplicationManager.getApplication().getComponent(RainbowStartupActivity::class.java)

        val isIntelliJHaskellEnabled: Boolean by lazy {
            PluginManagerCore.getPlugin(
                    PluginId.getId("intellij.haskell"))?.isEnabled ?: false
        }

    }
}