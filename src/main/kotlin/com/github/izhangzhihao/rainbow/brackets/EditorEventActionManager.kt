@file:Suppress("unused")

package com.github.izhangzhihao.rainbow.brackets

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryAdapter
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.util.Disposer
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class EditorEventActionManager : Disposable {

    private val executeOnEditorRelease = HashMap<Editor, () -> Unit>()
    private val listeners = ArrayList<EditorEventListener>()

    fun registerEditorEventListener(listener: EditorEventListener) {
        listeners.add(listener)
    }

    fun unregisterEditorEventListener(listener: EditorEventListener) {
        listeners.remove(listener)
    }

    fun install() {
        EditorFactory.getInstance().addEditorFactoryListener(object : EditorFactoryAdapter() {
            override fun editorCreated(event: EditorFactoryEvent) {
                val editor = event.editor
                val eventListener = EventListener(editor)
                val contentComponent = editor.contentComponent

                contentComponent.addKeyListener(eventListener)
                contentComponent.addFocusListener(eventListener)
                executeOnEditorRelease[editor] = {
                    contentComponent.removeKeyListener(eventListener)
                    contentComponent.removeFocusListener(eventListener)
                }
            }

            override fun editorReleased(event: EditorFactoryEvent) {
                executeOnEditorRelease.remove(event.editor)?.invoke()
            }
        }, this)
    }

    fun uninstall() {
        Disposer.dispose(this)
    }

    override fun dispose() {
        listeners.clear()
        executeOnEditorRelease.apply {
            forEach { _, action -> action() }
            clear()
        }
    }

    private inner class EventListener(private val editor: Editor) : KeyListener, FocusListener {

        override fun keyTyped(e: KeyEvent) {
            runEditorEventAction(e, EditorEventListener::onKeyTyped)
        }

        override fun keyPressed(e: KeyEvent) {
            runEditorEventAction(e, EditorEventListener::onKeyPressed)
        }

        override fun keyReleased(e: KeyEvent) {
            runEditorEventAction(e, EditorEventListener::onKeyReleased)
        }

        override fun focusGained(e: FocusEvent) {
            runEditorEventAction(e, EditorEventListener::onFocusGained)
        }

        override fun focusLost(e: FocusEvent) {
            runEditorEventAction(e, EditorEventListener::onFocusLost)
        }

        private inline fun <reified E> runEditorEventAction(
                e: E, action: EditorEventListener.(Editor, E) -> Unit) {
            val editor = editor
            if (!editor.isDisposed) {
                listeners.forEach { it.action(editor, e) }
            }
        }
    }

    companion object {
        val instance: EditorEventActionManager
            get() = ServiceManager.getService(EditorEventActionManager::class.java)
    }
}