/*
 * Copyright (c) 2017 Patrick Scheibe
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.izhangzhihao.rainbow.brackets.util

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.util.ErrorContext.Companion.fromThrowable
import com.intellij.AbstractBundle
import com.intellij.diagnostic.LogMessage
import com.intellij.diagnostic.ReportMessages
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.plugins.PluginUtil
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.application.impl.ApplicationInfoImpl
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.Consumer
import org.eclipse.egit.github.core.Issue
import org.eclipse.egit.github.core.Label
import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.IssueService
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.awt.Component
import java.util.*

private object AnonymousFeedback {
    private const val gitRepoUser = "izhangzhihao"

    //private const val gitRepoUser = "intellij-rainbow-brackets"
    private const val gitRepo = "intellij-rainbow-brackets"

    //private const val gitRepo = "bug"
    private const val issueLabel = "Auto generated"

    /**
     * Makes a connection to GitHub. Checks if there is an issue that is a duplicate and based on this, creates either a
     * new issue or comments on the duplicate (if the user provided additional information).
     *
     * @param environmentDetails Information collected by [getKeyValuePairs]
     * @return The report info that is then used in [GitHubErrorReporter] to show the user a balloon with the link
     * of the created issue.
     */
    internal fun sendFeedback(environmentDetails: MutableMap<String, String>): SubmittedReportInfo {
        try {
            val gitAccessToken = something
            val client = GitHubClient()
            client.setOAuth2Token(gitAccessToken)
            val repoID = RepositoryId(gitRepoUser, gitRepo)
            val issueService = IssueService(client)
            var newGibHubIssue = createNewGibHubIssue(environmentDetails)
            val duplicate = findFirstDuplicate(newGibHubIssue.title, issueService, repoID)
            var isNewIssue = true
            if (duplicate != null) {
                issueService.createComment(repoID, duplicate.number, newGibHubIssue.body)
                newGibHubIssue = duplicate
                isNewIssue = false
            } else newGibHubIssue = issueService.createIssue(repoID, newGibHubIssue)
            return SubmittedReportInfo(newGibHubIssue.htmlUrl, ErrorReportBundle.message(
                    if (isNewIssue) "git.issue.text" else "git.issue.duplicate.text", newGibHubIssue.htmlUrl, newGibHubIssue.number.toLong()),
                    if (isNewIssue) SubmissionStatus.NEW_ISSUE else SubmissionStatus.DUPLICATE)
        } catch (e: Exception) {
            e.printStackTrace()
            return SubmittedReportInfo(null,
                    ErrorReportBundle.message("report.error.connection.failure"),
                    SubmissionStatus.FAILED)
        }
    }

    private fun findFirstDuplicate(title: String, service: IssueService, repo: RepositoryId): Issue? {
        val openIssue = hashMapOf(IssueService.FILTER_STATE to IssueService.STATE_OPEN)
        val closedIssue = hashMapOf(IssueService.FILTER_STATE to IssueService.STATE_CLOSED)
        return service.pageIssues(repo, openIssue).flatten().firstOrNull { it.title == title }
                ?: service.pageIssues(repo, closedIssue).flatten().firstOrNull { it.title == title }
    }

    private fun createNewGibHubIssue(details: MutableMap<String, String>) = Issue().apply {
        val errorMessage = details.remove("error.message")?.takeIf(String::isNotBlank) ?: "Unspecified error"
        title = ErrorReportBundle.message("git.issue.title", errorMessage)
        body = generateGitHubIssueBody(details)
        labels = listOf(Label().apply { name = issueLabel })
    }

    private fun generateGitHubIssueBody(details: MutableMap<String, String>): String {
        val errorDescription = details.remove("error.description").orEmpty()
        val stackTrace = details.remove("error.stacktrace")?.takeIf(String::isNotBlank) ?: "invalid stacktrace"
        val result = StringBuilder()
        if (errorDescription.isNotEmpty()) {
            result.append(errorDescription)
            result.append("\n\n----------------------\n\n")
        }
        for ((key, value) in details) {
            result.append("- ")
            result.append(key)
            result.append(": ")
            result.append(value)
            result.append("\n")
        }
        result.append("- StackTrace:\n")
        result.append(stackTrace)
        return result.toString()
    }
}

//String(Base64.getEncoder().encode("".toByteArray()))
private const val st = "YjgwZGRiY2U2YTNlYTAzM2UyZGU" + "yNDcyNWEyZjE3MGQ2YThmMjc0MQ=="

private val something: String by lazy { String(Base64.getDecoder().decode(st)) }

class GitHubErrorReporter : ErrorReportSubmitter() {
    override fun getReportActionText() = ErrorReportBundle.message("report.error.to.plugin.vendor")
    override fun submit(
            events: Array<IdeaLoggingEvent>, info: String?, parent: Component, consumer: Consumer<SubmittedReportInfo>) =
            doSubmit(events[0], parent, consumer, fromThrowable(events[0].throwable), info)

    private fun doSubmit(
            event: IdeaLoggingEvent,
            parent: Component,
            callback: Consumer<SubmittedReportInfo>,
            errorContext: ErrorContext,
            description: String?): Boolean {
        val dataContext = DataManager.getInstance().getDataContext(parent)
        description?.let { errorContext.description = description }
        errorContext.message = event.message
        event.throwable?.let { throwable ->
            PluginUtil.getInstance().findPluginId(throwable)?.let { pluginId ->
                PluginManagerCore.getPlugin(pluginId)?.let { ideaPluginDescriptor ->
                    if (!ideaPluginDescriptor.isBundled) {
                        errorContext.pluginName = ideaPluginDescriptor.name
                        errorContext.pluginVersion = ideaPluginDescriptor.version
                    }
                }
            }
        }

        (event.data as? LogMessage)?.let { errorContext.attachments = it.includedAttachments }
        val project = CommonDataKeys.PROJECT.getData(dataContext)
        val reportValues = getKeyValuePairs(
                errorContext,
                ApplicationInfoImpl.getShadowInstance() as ApplicationInfoImpl,
                ApplicationNamesInfo.getInstance())
        val notifyingCallback = CallbackWithNotification(callback, project)
        val task = AnonymousFeedbackTask(project, ErrorReportBundle.message(
                "report.error.progress.dialog.text"), true, reportValues, notifyingCallback)
        if (project == null) task.run(EmptyProgressIndicator()) else ProgressManager.getInstance().run(task)
        return true
    }

    internal class CallbackWithNotification(
            private val consumer: Consumer<SubmittedReportInfo>,
            private val project: Project?) : Consumer<SubmittedReportInfo> {
        override fun consume(reportInfo: SubmittedReportInfo) {
            consumer.consume(reportInfo)
            if (reportInfo.status == SubmissionStatus.FAILED) ReportMessages.GROUP.createNotification(ReportMessages.getErrorReport(),
                    reportInfo.linkText, NotificationType.ERROR, null).setImportant(false).notify(project)
            else ReportMessages.GROUP.createNotification(ReportMessages.getErrorReport(), reportInfo.linkText,
                    NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER).setImportant(false).notify(project)
        }
    }
}

/**
 * Messages and strings used by the error reporter
 */
private object ErrorReportBundle {
    @NonNls
    private const val BUNDLE = "error-reporting.report-bundle"
    private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

    @JvmStatic
    internal fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
            AbstractBundle.message(bundle, key, *params)
}

private class AnonymousFeedbackTask(
        project: Project?, title: String, canBeCancelled: Boolean,
        private val params: MutableMap<String, String>,
        private val callback: Consumer<SubmittedReportInfo>) : Task.Backgroundable(project, title, canBeCancelled, DEAF) {
    override fun run(indicator: ProgressIndicator) {
        callback.consume(AnonymousFeedback.sendFeedback(params))
    }
}

/**
 * Collects information about the running IDEA and the error
 */
private fun getKeyValuePairs(
        errorContext: ErrorContext,
        appInfo: ApplicationInfoEx,
        namesInfo: ApplicationNamesInfo): MutableMap<String, String> {

    val params = mutableMapOf(
            "error.description" to errorContext.description,
            "Plugin Name" to errorContext.pluginName,
            "Plugin Version" to RainbowSettings.instance.version,
            "OS Name" to SystemInfo.OS_NAME,
            "OS Version" to SystemInfo.OS_VERSION,
            "Java Version" to SystemInfo.JAVA_VERSION,
            "App Name" to namesInfo.productName,
            "App Full Name" to namesInfo.fullProductName,
            "Is Snapshot" to java.lang.Boolean.toString(appInfo.build.isSnapshot),
            "App Build" to appInfo.build.asString(),
            "error.message" to errorContext.errorClass,
            "error.stacktrace" to "\n```\n" + errorContext.stackTrace + "\n```\n")
    for (attachment in errorContext.attachments) {
        params["attachment.${attachment.name}"] = attachment.path
        params["attachment.${attachment.name}.value"] = "\n```\n" + attachment.displayText + "\n```\n"
    }
    return params
}