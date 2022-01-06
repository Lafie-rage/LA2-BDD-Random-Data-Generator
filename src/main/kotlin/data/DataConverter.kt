package data

import data.first.model.FirstProposal
import data.second.model.KeyWord
import data.second.model.SecondProposals

/**
 * Convert a [List] of [FirstProposal] into a [List] of [KeyWord].
 */
fun convertKeywordsToProposals(propositionsFromFirstModel: List<FirstProposal>): List<KeyWord> {
    val begin = System.nanoTime()
    val keyWords = propositionsFromFirstModel.flatMap {
        it.keyWord
    }.toSet()

    val propositionsMappedWithKeywords = mutableMapOf<String, List<FirstProposal>>()
    keyWords.forEach { keyWord ->
        propositionsMappedWithKeywords[keyWord] = propositionsFromFirstModel.filter { proposition ->
            proposition.keyWord.contains(keyWord)
        }
    }

    val result = propositionsMappedWithKeywords.map {
        KeyWord(it.key, it.value.map { proposition -> proposition.asSecondModelProposals() })
    }

    val end = System.nanoTime()
    println("Took ${(end-begin)/1_000_000} milliseconds to run...")

    return result
}

/**
 * Convert a [FirstProposal] into a [SecondProposals].
 * Meaning that it removes the keyWord field.
 */
fun FirstProposal.asSecondModelProposals(): SecondProposals = SecondProposals(
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