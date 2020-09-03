package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.provider.PairedBraceProvider
import com.github.izhangzhihao.rainbow.brackets.util.memoize
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.lang.*
import com.intellij.psi.tree.IElementType

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
                                                ?.let { fileType -> BraceMatchingUtil.getBraceMatcher(fileType, language) as? PairedBraceMatcher }
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

                        val blackSet = providers.forLanguage(language)?.blackList?.map { it.toString() }?.toSet()

                        pairsList
                                ?.filter { !blackTokenTypes.contains(it.leftBraceType.toString()) }
                                ?.filter {
                                    if (blackSet != null) {
                                        !blackSet.contains(it.toString())
                                    } else {
                                        true
                                    }
                                }
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

    fun getBracePairs(language: Language): MutableMap<String, MutableList<BracePair>>? = bracePairs[language]

    private fun getBraceTypeSetOf(language: Language): Set<IElementType> = getBracePairs(language)?.values?.flatten()?.map { it -> listOf(it.leftBraceType, it.rightBraceType) }?.flatten()?.toSet() ?: emptySet()

    val braceTypeSet: (Language) -> Set<IElementType> = { language: Language -> getBraceTypeSetOf(language) }.memoize()
}

inline val Language.bracePairs: MutableMap<String, MutableList<BracePair>>?
    get() = BracePairs.getBracePairs(this)

inline val Language.braceTypeSet: Set<IElementType>
    get() = BracePairs.braceTypeSet(this)

val blackTokenTypes: Set<String> = setOf(
        // https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/423
        "php opening tag",
        "php closing tag",
        "php echo opening tag"
)