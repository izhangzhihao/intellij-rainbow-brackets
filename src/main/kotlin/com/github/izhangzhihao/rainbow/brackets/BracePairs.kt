/*
 * BracePairs
 *
 * Created by Yii.Guxing on 2018/01/25
 */

package com.github.izhangzhihao.rainbow.brackets

import com.intellij.lang.BracePair
import com.intellij.lang.Language
import com.intellij.lang.LanguageBraceMatching

object BracePairs {

    private lateinit var bracePairs: Map<Language, List<BracePair>?>

    fun init() {
        bracePairs = Language.getRegisteredLanguages()
                .map {
                    it to LanguageBraceMatching
                            .INSTANCE
                            .forLanguage(it)
                            ?.pairs
                            ?.toList()
                }
                .toMap()
    }

    fun getBracePairs(language: Language) = bracePairs[language]

}

inline val Language.bracePairs: List<BracePair>?
    get() = BracePairs.getBracePairs(this)