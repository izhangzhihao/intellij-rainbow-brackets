/*
 * BracePairs
 *
 * Created by Yii.Guxing on 2018/01/25
 */

package com.github.izhangzhihao.rainbow.brackets

import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.lang.*

object BracePairs {

    private lateinit var bracePairs: Map<Language, List<BracePair>?>

    fun init() {
        bracePairs = Language.getRegisteredLanguages()
                .map { language ->
                    if (language is CompositeLanguage) {
                        return@map language to null
                    }

                    var pairs = LanguageBraceMatching.INSTANCE.forLanguage(language)?.pairs
                    if (pairs == null || pairs.isEmpty()) {
                        pairs = language.associatedFileType
                                ?.let { BraceMatchingUtil.getBraceMatcher(it, language) as? PairedBraceMatcher }
                                ?.pairs
                    }

                    language to pairs?.toList()
                }
                .toMap()
    }

    fun getBracePairs(language: Language) = bracePairs[language]

}

inline val Language.bracePairs: List<BracePair>?
    get() = BracePairs.getBracePairs(this)