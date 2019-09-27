package com.github.izhangzhihao.rainbow.brackets.indents

import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.components.AbstractProjectComponent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class RainbowIndentsPassFactory(project: Project, registrar: TextEditorHighlightingPassRegistrar) :
        AbstractProjectComponent(project), TextEditorHighlightingPassFactory {

    init {
        registrar.registerTextEditorHighlightingPass(
                this,
                TextEditorHighlightingPassRegistrar.Anchor.LAST,
                Pass.LAST_PASS,
                false,
                false
        )
    }

    override fun getComponentName(): String = "RainbowIndentsPassFactory"

    override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass {
        return RainbowIndentsPass(myProject, editor, file)
    }
}