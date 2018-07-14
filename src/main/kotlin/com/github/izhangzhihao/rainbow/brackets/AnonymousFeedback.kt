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
package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.CommonBundle
import com.intellij.diagnostic.IdeErrorsDialog
import com.intellij.diagnostic.LogMessageEx
import com.intellij.diagnostic.ReportMessages
import com.intellij.errorreport.bean.ErrorBean
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.PluginManager
import com.intellij.idea.IdeaLogger
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.ApplicationNamesInfo.getComponentName
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
import com.intellij.openapi.util.JDOMUtil
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
    internal fun sendFeedback(environmentDetails: Map<String, String>): SubmittedReportInfo {
        try {
            val gitAccessToken = something
            val client = GitHubClient()
            client.setOAuth2Token(gitAccessToken)
            val repoID = RepositoryId(gitRepoUser, gitRepo)
            val issueService = IssueService(client)
            var newGibHubIssue = createNewGibHubIssue(environmentDetails)
            val errorMessage = environmentDetails.getOrDefault("error.message", "Unspecified error")
            val duplicate = findFirstDuplicate(errorMessage, issueService, repoID)
            var isNewIssue = true
            if (duplicate != null) {
                issueService.createComment(repoID, duplicate.number, generateGitHubIssueBody(environmentDetails))
                newGibHubIssue = duplicate
                isNewIssue = false
            } else newGibHubIssue = issueService.createIssue(repoID, newGibHubIssue)
            return SubmittedReportInfo(newGibHubIssue.htmlUrl, ErrorReportBundle.message(
                    if (isNewIssue) "git.issue.text" else "git.issue.duplicate.text", newGibHubIssue.htmlUrl, newGibHubIssue.number.toLong()),
                    if (isNewIssue) SubmissionStatus.NEW_ISSUE else SubmissionStatus.DUPLICATE)
        } catch (e: Exception) {
            return SubmittedReportInfo(null,
                    ErrorReportBundle.message("report.error.connection.failure"),
                    SubmissionStatus.FAILED)
        }
    }

    private fun findFirstDuplicate(title: String, service: IssueService, repo: RepositoryId): Issue? {
        val searchIssue = (service.searchIssues(repo, "open", title).firstOrNull()
                ?: service.searchIssues(repo, "closed", title).firstOrNull())
        return searchIssue?.let {
            val issue = Issue()
            issue.htmlUrl = it.htmlUrl
            issue.number = it.number
            issue
        }
    }

    private fun createNewGibHubIssue(details: Map<String, String>) = Issue().apply {
        val errorMessage = details.getOrDefault("error.message", "Unspecified error")
        title = ErrorReportBundle.message("git.issue.title", errorMessage)
        body = generateGitHubIssueBody(details)
        labels = listOf(Label().apply { name = issueLabel })
    }

    private fun generateGitHubIssueBody(details: Map<String, String>): String {
        val errorDescription = details.getOrDefault("error.description", "")
        val stackTrace = details.getOrDefault("error.stacktrace", "invalid stacktrace")
        val result = StringBuilder()
        if (!errorDescription.isEmpty()) {
            result.append(errorDescription)
            result.append("\n\n----------------------\n\n")
        }
        details.filterKeys { it != "error.stacktrace" }
                .forEach { (key, value) ->
                    result.append("- ")
                    result.append(key)
                    result.append(": ")
                    result.append(value)
                    result.append("\n")
                }
        result.append("\n```\n")
        result.append(stackTrace)
        result.append("\n```\n")
        return result.toString()
    }
}

private const val something = "0dc15c045662cb07366" + "c36903d05d7603e941456"

class GitHubErrorReporter : ErrorReportSubmitter() {
    override fun getReportActionText() = ErrorReportBundle.message("report.error.to.plugin.vendor")
    override fun submit(
            events: Array<IdeaLoggingEvent>, info: String?, parent: Component, consumer: Consumer<SubmittedReportInfo>) =
            doSubmit(events[0], parent, consumer, ErrorBean(events[0].throwable, IdeaLogger.ourLastActionId), info)

    private fun doSubmit(
            event: IdeaLoggingEvent,
            parent: Component,
            callback: Consumer<SubmittedReportInfo>,
            bean: ErrorBean,
            description: String?): Boolean {
        val dataContext = DataManager.getInstance().getDataContext(parent)
        bean.description = description
        bean.message = event.message
        event.throwable?.let { throwable ->
            IdeErrorsDialog.findPluginId(throwable)?.let { pluginId ->
                PluginManager.getPlugin(pluginId)?.let { ideaPluginDescriptor ->
                    if (!ideaPluginDescriptor.isBundled) {
                        bean.pluginName = ideaPluginDescriptor.name
                        bean.pluginVersion = ideaPluginDescriptor.version
                    }
                }
            }
        }

        (event.data as? LogMessageEx)?.let { bean.attachments = it.includedAttachments }
        val project = CommonDataKeys.PROJECT.getData(dataContext)
        val reportValues = getKeyValuePairs(
                bean,
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
            if (reportInfo.status == SubmissionStatus.FAILED) ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT,
                    reportInfo.linkText, NotificationType.ERROR, null).setImportant(false).notify(project)
            else ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT, reportInfo.linkText,
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
            CommonBundle.message(bundle, key, *params)
}

private class AnonymousFeedbackTask(
        project: Project?, title: String, canBeCancelled: Boolean,
        private val params: Map<String, String>,
        private val callback: Consumer<SubmittedReportInfo>) : Task.Backgroundable(project, title, canBeCancelled, DEAF) {
    override fun run(indicator: ProgressIndicator) {
        callback.consume(AnonymousFeedback.sendFeedback(params))
    }
}

/**
 * Collects information about the running IDEA and the error
 */
private fun getKeyValuePairs(
        error: ErrorBean,
        appInfo: ApplicationInfoImpl,
        namesInfo: ApplicationNamesInfo): Map<String, String> {

    val resource = "/idea/" + getComponentName() + ".xml"

    val doc = JDOMUtil.loadDocument(ApplicationNamesInfo::class.java, resource)
    val rootElement = doc.rootElement
    val names = rootElement.getChild("names", rootElement.namespace)
    val myProductName = names.getAttributeValue("product")
    val myFullProductName = names.getAttributeValue("fullname", myProductName)

    val params: MutableMap<String, String?> = mutableMapOf(
            "error.description" to error.description,
            "Plugin Name" to error.pluginName,
            "Plugin Version" to RainbowSettings.instance.version,
            "OS Name" to SystemInfo.OS_NAME,
            "OS Version" to SystemInfo.OS_VERSION,
            "Java Version" to SystemInfo.JAVA_VERSION,
            "App Name" to namesInfo.productName,
            "App Full Name" to myFullProductName,
            "Is Snapshot" to java.lang.Boolean.toString(appInfo.build.isSnapshot),
            "App Build" to appInfo.build.asString(),
            "Last Action" to error.lastAction,
            "error.message" to error.message,
            "error.stacktrace" to error.stackTrace)
    for (attachment in error.attachments) {
        params["attachment.name"] = attachment.path
        params["attachment.value"] = attachment.displayText
    }

    return params.mapValues { it -> it.value ?: "" }
}