package com.github.izhangzhihao.rainbow.brackets

import com.intellij.openapi.editor.Editor
import java.awt.event.FocusEvent
import java.awt.event.KeyEvent
import java.util.*

/**
 * EditorEventListener
 *
 * Created by Yii.Guxing on 2018/05/13
 */
interface EditorEventListener : EventListener {

    /**
     * Invoked when a key has been typed.
     */
    fun onKeyTyped(editor: Editor, keyEvent: KeyEvent) {}

    /**
     * Invoked when a key has been pressed.
     */
    fun onKeyPressed(editor: Editor, keyEvent: KeyEvent) {}

    /**
     * Invoked when a key has been released.
     */
    fun onKeyReleased(editor: Editor, keyEvent: KeyEvent) {}

    /**
     * Invoked when an [Editor] gains the keyboard focus.
     */
    fun onFocusGained(editor: Editor, focusEvent: FocusEvent) {}

    /**
     * Invoked when an [Editor] loses the keyboard focus.
     */
    fun onFocusLost(editor: Editor, focusEvent: FocusEvent) {}

}