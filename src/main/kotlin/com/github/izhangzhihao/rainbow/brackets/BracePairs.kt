package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.provider.PairedBraceProvider
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.lang.*

object BracePairs {

    @Suppress("MemberVisibilityCanBePrivate")
    val providers = LanguageExtension<PairedBraceProvider>("izhangzhihao.rainbow.brackets.pairedBraceProvider")

    private val bracePairs =
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

                        val braceMap: MutableMap<String, MutableList<BracePair>> = mutableMapOf()

                        pairsList
                                ?.map { listOf(Pair(it.leftBraceType.toString(), it), Pair(it.rightBraceType.toString(), it)) }
                                ?.flatten()
                                ?.forEach { it ->
                                    val bracePairs = braceMap[it.first]
                                    if (bracePairs == null) {
                                        braceMap[it.first] = mutableListOf(it.second)
                                    } else {
                                        bracePairs.add(it.second)
                                    }
                                }

                        language to braceMap
                    }
                    .toMap()

    fun getBracePairs(language: Language) = bracePairs[language]
}

inline val Language.bracePairs: MutableMap<String, MutableList<BracePair>>?
    get() = BracePairs.getBracePairs(this)