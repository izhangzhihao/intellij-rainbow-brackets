package com.github.izhangzhihao.rainbow.brackets

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.lang.BracePair
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType

/**
 * DefaultRainbowVisitor
 *
 * Created by Yii.Guxing on 2018/1/23
 */
class DefaultRainbowVisitor : RainbowHighlightVisitor() {

    private val levelCacheKeyMap: MutableMap<BracePair, Key<Int?>> = mutableMapOf()

    private val BracePair.levelCacheKey: Key<Int?>
        get() = levelCacheKeyMap[this] ?: Key.create<Int?>("$leftBraceType:$rightBraceType:$isStructural").also {
            levelCacheKeyMap[this] = it
        }

    override fun clone(): HighlightVisitor = DefaultRainbowVisitor()

    override fun visit(element: PsiElement) {
        val type = (element as? LeafPsiElement)?.elementType ?: return
        val pairs = element.language.bracePairs ?: return
        val pair = pairs.find { it.leftBraceType == type || it.rightBraceType == type } ?: return

        with(element) {
            val level = pair.levelCacheKey[parent] ?: getBracketLevel(type, pair).also {
                if (it >= 0) {
                    pair.levelCacheKey[parent] = it
                }
            }
            if (level >= 0) {
                setHighlightInfo(level)
            }
        }
    }

    companion object {
        // TODO 优化：消除局部方法
        // FIXME 应该不着色未配对成功的括号
        private fun LeafPsiElement.getBracketLevel(type: IElementType, pair: BracePair): Int {
            var level = if (type == pair.rightBraceType) 1 else 0

            tailrec fun iterateParents(currentNode: PsiElement) {

                tailrec fun iterateChildren(currentChild: PsiElement) {
                    if (currentChild is LeafPsiElement) {
                        when (currentChild.elementType) {
                            pair.leftBraceType -> level++
                            pair.rightBraceType -> level--
                        }
                    }
                    if ((currentChild != currentNode) && (currentChild != currentNode.parent.lastChild)) {
                        iterateChildren(currentChild.nextSibling)
                    }
                }

                if (currentNode.parent !is PsiFile) {
                    iterateChildren(currentNode.parent.firstChild)
                    iterateParents(currentNode.parent)
                }
            }

            iterateParents(this)
            return level - 1
        }
    }
}