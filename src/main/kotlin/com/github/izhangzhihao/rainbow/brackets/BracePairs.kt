package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.provider.PairedBraceProvider
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.lang.*

object BracePairs {

    @Suppress("MemberVisibilityCanBePrivate")
    val providers = LanguageExtension<PairedBraceProvider>("izhangzhihao.rainbow.brackets.pairedBraceProvider")

    private val bracePairs by lazy {
        Language.getRegisteredLanguages()
                .map { language ->
                    if (language is CompositeLanguage) {
                        return@map language to null
                    }

                    val pairs =
                            LanguageBraceMatching.INSTANCE.forLanguage(language)?.pairs.let {
                                if (it == null || it.isEmpty()) {
                                    language.associatedFileType
                                            ?.let { BraceMatchingUtil.getBraceMatcher(it, language) as? PairedBraceMatcher }
                                            ?.pairs
                                } else {
                                    it
                                }
                            }

                    val pairsList = providers.forLanguage(language)?.pairs?.let {
                        if (pairs != null && pairs.isNotEmpty()) {
                            it.toMutableSet().apply { addAll(pairs) }
                        } else {
                            it
                        }
                    } ?: pairs?.toList()

                    val braceMap = pairsList
                            ?.map { listOf(Pair(it.leftBraceType.toString(), it), Pair(it.rightBraceType.toString(), it)) }
                            ?.flatten()
                            ?.toMap()

                    language to braceMap
                }
                .toMap()
    }

    fun getBracePairs(language: Language) = bracePairs[language]

}

inline val Language.bracePairs: Map<String, BracePair>?
    get() = BracePairs.getBracePairs(this)