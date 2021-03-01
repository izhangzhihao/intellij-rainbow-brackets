package com.github.izhangzhihao.rainbow.brackets.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import java.io.File

fun File.toPsiFile(project: Project): PsiFile? = toVirtualFile()?.toPsiFile(project)

fun File.toVirtualFile(): VirtualFile? = LocalFileSystem.getInstance().findFileByIoFile(this)

fun File.toPsiDirectory(project: Project): PsiDirectory? {
    return toVirtualFile()?.let { vfile -> PsiManager.getInstance(project).findDirectory(vfile) }
}

fun VirtualFile.toPsiFile(project: Project): PsiFile? = PsiManager.getInstance(project).findFile(this)

fun VirtualFile.toPsiDirectory(project: Project): PsiDirectory? = PsiManager.getInstance(project).findDirectory(this)