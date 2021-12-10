package data

import data.first.model.FirstProposition
import data.second.model.KeyWord
import data.second.model.SecondProposition

/**
 * Convert a [List] of [FirstProposition] into a [List] of [KeyWord].
 */
fun convertKeywordsToPropositions(propositionsFromFirstModel: List<FirstProposition>): List<KeyWord> {
    val begin = System.nanoTime()
    val keyWords = propositionsFromFirstModel.flatMap {
        it.keyWord
    }.toSet()

    val propositionsMappedWithKeywords = mutableMapOf<String, List<FirstProposition>>()
    keyWords.forEach { keyWord ->
        propositionsMappedWithKeywords[keyWord] = propositionsFromFirstModel.filter { proposition ->
            proposition.keyWord.contains(keyWord)
        }
    }

    val result = propositionsMappedWithKeywords.map {
        KeyWord(it.key, it.value.map { proposition -> proposition.asSecondModelProposition() })
    }

    val end = System.nanoTime()
    println("Took ${(end-begin)/1_000_000} milliseconds to run...")

    return result
}

/**
 * Convert a [FirstProposition] into a [SecondProposition].
 * Meaning that it removes the keyWord field.
 */
fun FirstProposition.asSecondModelProposition(): SecondProposition = SecondProposition(
    this.id,
    this.userId,
    this.beginDate,
    this.endDate,
    this.value,
    this.isGeneric,
    this.isGoods,
    this.name,
    this.description,
    this.competence,
)