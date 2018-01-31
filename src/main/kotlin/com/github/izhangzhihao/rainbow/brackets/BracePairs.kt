/*
 * BracePairs
 *
 * Created by Yii.Guxing on 2018/01/25
 */

package com.github.izhangzhihao.rainbow.brackets

import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.lang.*

/**
 * PairedBraceProvider
 *
 * Created by Yii.Guxing on 2018/1/30
 */
interface PairedBraceProvider {
    val pairs: List<BracePair>
}

object BracePairs {

    private lateinit var bracePairs: Map<Language, List<BracePair>?>

    @Suppress("MemberVisibilityCanBePrivate")
    val providers = LanguageExtension<PairedBraceProvider>("izhangzhihao.rainbow.brackets.pairedBraceProvider")

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

                    val pairsList = providers.forLanguage(language)?.pairs?.let {
                        if (pairs != null && pairs.isNotEmpty()) {
                            it.toMutableSet().apply { addAll(pairs) }.toList()
                        } else {
                            it
                        }
                    } ?: pairs?.toList()

                    language to pairsList
                }
                .toMap()
    }

    fun getBracePairs(language: Language) = bracePairs[language]

}

inline val Language.bracePairs: List<BracePair>?
    get() = BracePairs.getBracePairs(this)