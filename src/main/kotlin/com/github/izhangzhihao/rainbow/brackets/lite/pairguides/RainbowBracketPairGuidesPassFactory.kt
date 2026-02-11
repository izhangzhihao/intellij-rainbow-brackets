package com.github.izhangzhihao.rainbow.brackets.lite.pairguides

import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class RainbowBracketPairGuidesPassFactory :
    TextEditorHighlightingPassFactoryRegistrar, TextEditorHighlightingPassFactory {

    override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass {
        return RainbowBracketPairGuidesPass(file.project, editor, file)
    }

    override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
        registrar.registerTextEditorHighlightingPass(
            this,
            TextEditorHighlightingPassRegistrar.Anchor.BEFORE,
            Pass.UPDATE_FOLDING,
            false,
            false
        )
    }
}
