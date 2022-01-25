package data

import data.propositions.first.model.FirstProposal
import data.propositions.second.model.KeyWord
import data.propositions.second.model.SecondProposals

/**
 * Convert a [List] of [FirstProposal] into a [List] of [KeyWord].
 *
 * @param firstModelProposal A list of [FirstProposal] to convert into the second model.
 *
 * @return The list of [KeyWord] created from the list given as argument.
 */
fun convertKeywordsToProposals(firstModelProposal: List<FirstProposal>): List<KeyWord> {
    val begin = System.nanoTime()
    val keyWords = firstModelProposal.flatMap {
        it.keyWord
    }.toSet()

    val propositionsMappedWithKeywords = mutableMapOf<String, List<FirstProposal>>()
    keyWords.forEach { keyWord ->
        propositionsMappedWithKeywords[keyWord] = firstModelProposal.filter { proposition ->
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
 *
 * @return The corresponding proposal as [SecondProposals].
 */
fun FirstProposal.asSecondModelProposals(): SecondProposals = SecondProposals(
    id = this.id,
    userId = this.userId,
    beginDate = this.beginDate,
    endDate = this.endDate,
    value = this.value,
    isGeneric = this.isGeneric,
    isGoods = this.isGoods,
    name = this.name,
    description = this.description,
    competence = this.competence,
)