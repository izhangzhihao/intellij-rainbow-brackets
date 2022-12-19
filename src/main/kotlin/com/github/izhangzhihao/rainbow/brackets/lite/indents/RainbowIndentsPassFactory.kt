package com.github.izhangzhihao.rainbow.brackets.lite.indents

import com.intellij.codeHighlighting.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class RainbowIndentsPassFactory :
        TextEditorHighlightingPassFactoryRegistrar, TextEditorHighlightingPassFactory {

    override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass {
        return RainbowIndentsPass(file.project, editor, file)
    }

    override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
        registrar.registerTextEditorHighlightingPass(
                this,
                TextEditorHighlightingPassRegistrar.Anchor.LAST,
                Pass.LAST_PASS,
                false,
                false
        )
    }
}