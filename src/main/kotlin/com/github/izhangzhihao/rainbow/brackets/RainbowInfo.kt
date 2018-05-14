package com.github.izhangzhihao.rainbow.brackets

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.awt.Color
import java.lang.ref.WeakReference

/**
 * RainbowInfo
 *
 * Created by Yii.Guxing on 2018/05/12
 */
data class RainbowInfo(var level: Int, var color: Color) {
    private var _startElement: WeakReference<PsiElement>? = null
    private var _endElement: WeakReference<PsiElement>? = null

    var startElement: PsiElement?
        get() = _startElement?.get()
        set(value) {
            _startElement = value?.let { WeakReference(it) }
        }

    val startOffset get() = startElement?.startOffset ?: -1

    var endElement: PsiElement?
        get() = _endElement?.get()
        set(value) {
            _endElement = value?.let { WeakReference(it) }
        }

    val endOffset get() = endElement?.endOffset ?: -1

    fun containsOffset(offset: Int): Boolean {
        val startElement = startElement ?: return false
        val endElement = endElement ?: return false
        val startOffset = startElement.startOffset
        val endOffset = endElement.endOffset

        return offset in startOffset..endOffset
    }

    companion object {
        val KEY_RAINBOW: Key<RainbowInfo> = Key.create("RAINBOW_INFO")
    }
}