/*
 * Copyright 2000-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.github.izhangzhihao.rainbow.brackets.util

import com.github.izhangzhihao.rainbow.brackets.RainbowUpdateNotifyActivity.Companion.getPlugin
import com.github.izhangzhihao.rainbow.brackets.RainbowUpdateNotifyActivity.Companion.getPluginId
import com.github.izhangzhihao.rainbow.brackets.RainbowUpdateNotifyActivity.Companion.getPluginVersion
import com.intellij.ide.plugins.*
import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.PermanentInstallationID
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.updateSettings.impl.PluginDownloader
import com.intellij.openapi.updateSettings.impl.UpdateSettings
import com.intellij.openapi.util.JDOMUtil
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.util.Alarm
import com.intellij.util.io.HttpRequests
import com.intellij.util.text.VersionComparatorUtil
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

sealed class PluginUpdateStatus {

    object LatestVersionInstalled : PluginUpdateStatus()

    class Update(
            val pluginDescriptor: IdeaPluginDescriptor,
            val hostToInstallFrom: String?
    ) : PluginUpdateStatus()

    class CheckFailed(val message: String, val detail: String? = null) : PluginUpdateStatus()

    companion object {
        fun fromException(message: String, e: Exception): PluginUpdateStatus {
            val writer = StringWriter()
            e.printStackTrace(PrintWriter(writer))
            return CheckFailed(message, writer.toString())
        }
    }
}

class RainbowBracketsPluginUpdater : Disposable {
    private var updateDelay = INITIAL_UPDATE_DELAY
    private val alarm = Alarm(Alarm.ThreadToUse.POOLED_THREAD, this)
    private val notificationGroup = NotificationGroup(
            "RainbowBrackets plugin updates",
            NotificationDisplayType.STICKY_BALLOON, true
    )

    @Volatile
    private var checkQueued = false

    @Volatile
    private var lastUpdateStatus: PluginUpdateStatus? = null

    fun runCheckUpdate() {
        if (ApplicationManager.getApplication().isUnitTestMode || ApplicationManager.getApplication().isHeadlessEnvironment) return
        if (!UpdateSettings.getInstance().isCheckNeeded) return

        val lastUpdateTime = java.lang.Long.parseLong(PropertiesComponent.getInstance().getValue(PROPERTY_NAME, "0"))
        if (lastUpdateTime == 0L || System.currentTimeMillis() - lastUpdateTime > CACHED_REQUEST_DELAY) {
            queueUpdateCheck { updateStatus ->
                when (updateStatus) {
                    is PluginUpdateStatus.Update -> notifyPluginUpdateAvailable(updateStatus)
                    is PluginUpdateStatus.CheckFailed -> LOG.info("Plugin update check failed: ${updateStatus.message}, details: ${updateStatus.detail}")
                }
                true
            }
        }
    }

    private fun queueUpdateCheck(callback: (PluginUpdateStatus) -> Boolean) {
        ApplicationManager.getApplication().assertIsDispatchThread()
        if (!checkQueued) {
            checkQueued = true
            alarm.addRequest({ updateCheck(callback) }, updateDelay)
            updateDelay *= 2 // exponential backoff
        }
    }

    private fun updateCheck(callback: (PluginUpdateStatus) -> Boolean) {
        val updateStatus: PluginUpdateStatus
        updateStatus = try {
            checkUpdatesInMainRepository()
        } catch (e: Exception) {
            PluginUpdateStatus.fromException("RainbowBrackets plugin update check failed", e)
        }

        lastUpdateStatus = updateStatus
        checkQueued = false

        if (updateStatus !is PluginUpdateStatus.CheckFailed) {
            recordSuccessfulUpdateCheck()
        }

        ApplicationManager.getApplication().invokeLater({
            callback(updateStatus)
        }, ModalityState.any())
    }

    private fun initPluginDescriptor(newVersion: String): IdeaPluginDescriptor {
        val originalPlugin = PluginManagerCore.getPlugin(getPluginId())!!
        return PluginNode(getPluginId()).apply {
            version = newVersion
            name = originalPlugin.name
            description = originalPlugin.description
        }
    }

    private fun checkUpdatesInMainRepository(): PluginUpdateStatus {
        val buildNumber = ApplicationInfo.getInstance().apiVersion
        val currentVersion = getPlugin()?.version
        val os = URLEncoder.encode(SystemInfo.OS_NAME + " " + SystemInfo.OS_VERSION, CharsetToolkit.UTF8)
        val uid = PermanentInstallationID.get()
        val pluginId = getPluginId().idString
        val url =
                "https://plugins.jetbrains.com/plugins/list?pluginId=$pluginId&build=$buildNumber&pluginVersion=$currentVersion&os=$os&uuid=$uid"
        val responseDoc = HttpRequests.request(url).connect {
            JDOMUtil.load(it.inputStream)
        }
        if (responseDoc.name != "plugin-repository") {
            return PluginUpdateStatus.CheckFailed(
                    "Unexpected plugin repository response",
                    JDOMUtil.writeElement(responseDoc, "\n")
            )
        }
        if (responseDoc.children.isEmpty()) {
            // No plugin version compatible with current IDEA build; don't retry updates
            return PluginUpdateStatus.LatestVersionInstalled
        }
        val newVersion = responseDoc.getChild("category")?.getChild("idea-plugin")?.getChild("version")?.text
                ?: return PluginUpdateStatus.CheckFailed(
                        "Couldn't find plugin version in repository response",
                        JDOMUtil.writeElement(responseDoc, "\n")
                )
        val pluginDescriptor = initPluginDescriptor(newVersion)
        return updateIfNotLatest(pluginDescriptor, null)
    }

    private fun updateIfNotLatest(RainbowBracketsPlugin: IdeaPluginDescriptor, host: String?): PluginUpdateStatus {
        if (VersionComparatorUtil.compare(RainbowBracketsPlugin.version, getPluginVersion()) <= 0) {
            return PluginUpdateStatus.LatestVersionInstalled
        }

        return PluginUpdateStatus.Update(RainbowBracketsPlugin, host)
    }

    private fun recordSuccessfulUpdateCheck() {
        PropertiesComponent.getInstance().setValue(PROPERTY_NAME, System.currentTimeMillis().toString())
        updateDelay = INITIAL_UPDATE_DELAY
    }

    private fun notifyPluginUpdateAvailable(update: PluginUpdateStatus.Update) {
        val notification = notificationGroup.createNotification(
                "RainbowBrackets",
                "A new version ${update.pluginDescriptor.version} of the RainbowBrackets plugin is available. <b><a href=\"#\">Install</a></b>",
                NotificationType.INFORMATION,
                NotificationListener { notification, _ ->
                    notification.expire()
                    installPluginUpdate(update)
                })

        notification.notify(null)
    }

    fun installPluginUpdate(
            update: PluginUpdateStatus.Update,
            cancelCallback: () -> Unit = {}
    ) {
        val descriptor = update.pluginDescriptor
        val pluginDownloader = PluginDownloader.createDownloader(descriptor, update.hostToInstallFrom, null)
        ProgressManager.getInstance().run(object : Task.Backgroundable(
                null, "Downloading plugins", true, PluginManagerUISettings.getInstance()
        ) {
            override fun run(indicator: ProgressIndicator) {
                val prepareResult = try {
                    pluginDownloader.prepareToInstall(indicator)
                } catch (e: IOException) {
                    LOG.info(e)
                    false
                }

                if (prepareResult) {
                    pluginDownloader.install()

                    ApplicationManager.getApplication().invokeLater {
                        PluginManagerMain.notifyPluginsUpdated(null)
                    }
                }
            }

            override fun onCancel() {
                cancelCallback()
            }
        })
    }

    override fun dispose() {
    }

    companion object {
        private const val INITIAL_UPDATE_DELAY = 2000L
        private val CACHED_REQUEST_DELAY = TimeUnit.DAYS.toMillis(1)

        private const val PROPERTY_NAME = "rainbow.brackets.lastUpdateCheck"
        private val LOG = Logger.getInstance(RainbowBracketsPluginUpdater::class.java)

        fun getInstance(): RainbowBracketsPluginUpdater = ServiceManager.getService(RainbowBracketsPluginUpdater::class.java)
    }
}