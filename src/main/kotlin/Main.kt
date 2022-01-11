import com.google.gson.Gson
import data.common.model.Competence
import data.convertKeywordsToProposals
import data.first.model.FirstProposal
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future

private const val LOREM_IPSUM_PATH = "./src/main/resources/lorem_ipsum.txt"
private const val PROPOSALS_PATH = "./src/main/resources/proposals.json"
private const val KEY_WORDS_PATH = "./src/main/resources/keyWords.json"
private const val DEFAULT_DATE: String = "01/01/2000 00:00:00"
private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
private val defaultAfterDate = dateFormat.parse(DEFAULT_DATE)
private val regexDelimiter = Regex("(([.]*[,]*[ ]|\n)+|[.])")
private val words = File(LOREM_IPSUM_PATH).readText().split(regexDelimiter).filter { it.isNotBlank() }
private val random = Random()
private val gson = Gson()

private var competenceCounter = 0

fun main(args: Array<String>) {
    if(args.isEmpty() || args[0].toIntOrNull() == null) {
        println("Please enter the size of the data set that you want to generate as an integer")
        return
    }

    val dataSetSize = args[0].toInt()
    // Retrieve the number of cores available on your computer
    val numberCoresAvailable = Runtime.getRuntime().availableProcessors()
    val executors = Executors.newFixedThreadPool(numberCoresAvailable)

    val dataSetSizeForEachThread = dataSetSize/numberCoresAvailable

    println("There's $numberCoresAvailable core(s) available.")
    println("Spreading generation on your different cores...")

    val globalProposals = mutableListOf<FirstProposal>()
    val results = mutableListOf<Future<List<FirstProposal>>>()
    for(threadIndex in 0 until numberCoresAvailable) {

        val result = executors.submit<List<FirstProposal>> {
            val proposals = mutableListOf<FirstProposal>()
            // Generate a random data set for each defined models.
            // The size of the data set depends on the value given as argument when calling the program.
            val begin = System.nanoTime()
            println("Generation started on thread $threadIndex")
            for(i in 0 until dataSetSizeForEachThread) {
                // The id given is the current index of the loop + the position where the item should go one the list
                // Considering a list of 20 elements divided in 4 threads
                // First part would be from 0 to 4, second from 5 to 9
                // third from 10 to 14 and fourth from 15 to 19
                // The size of the data set for each thread would be 5
                val id = i + (threadIndex * dataSetSizeForEachThread)
                proposals.add(getRandomFirstProposal(id, dataSetSize))
            }
            println("Generation done on thread $threadIndex")
            val end = System.nanoTime()
            println("First model generation done after ${(end - begin) / 1_000_000} milliseconds.")

            return@submit proposals
        }
        results.add(result)
    }

    results.forEach { future ->
        globalProposals.addAll(future.get())
    }

    executors.execute {
        println("Writing first model data...")
        val proposalsAsJson = gson.toJson(globalProposals)
        val threadWorkBegin = System.nanoTime()
        File(PROPOSALS_PATH).writeText(proposalsAsJson.toString())
        val threadWordEnd = System.nanoTime()
        println("First model file written after ${(threadWordEnd - threadWorkBegin) / 1_000_000} milliseconds.")
    }

    println("Converting from first model to second one...")
    var begin = System.nanoTime()
    val globalKeyWords = convertKeywordsToProposals(globalProposals)
    println("Conversion done.")
    var end = System.nanoTime()
    println("Conversion done after ${(end - begin) / 1_000_000} milliseconds.")

    println("First model :")
    println("Size : ${globalProposals.size}")

    println("Second model :")
    println("Size : ${globalKeyWords.size}")

    println("Writing second model data...")
    val keyWordAsJson = gson.toJson(globalKeyWords)
    begin = System.nanoTime()
    File(KEY_WORDS_PATH).writeText(keyWordAsJson.toString())
    end = System.nanoTime()
    println("Second model file written after ${(end - begin) / 1_000_000} milliseconds.")

    // Properly shutdown used threads
    executors.shutdown()
}

/**
 * Generate a random [FirstProposal] by randomly retrieving the data needed to instantiate it.
 *
 * @param id The id of the proposal
 * @param dataSetSize The size of the data set that you want to generate.
 *
 * @return The randomly generated proposal
 */
private fun getRandomFirstProposal(id: Int, dataSetSize: Int): FirstProposal {
    val beginDate = getRandomDate()
    return FirstProposal(
        id = id,
        userId = getRandomInteger(max = dataSetSize),
        beginDate = beginDate,
        endDate = getRandomDate(beginDate),
        value = getRandomFloat(dataSetSize.toFloat()),
        isGeneric = getRandomBoolean(),
        isGoods = getRandomBoolean(),
        name = getRandomWord(),
        description = getRandomSentences(),
        competence = getRandomCompetence(),
        keyWord = getRandomKeyWords(dataSetSize),
    )
}

/**
 * Generate a random [Competence] by randomly retrieving the data needed to instantiate it.
 *
 * @return The randomly generated competence.
 *
 * @see getRandomWord
 * @see Competence
 */
private fun getRandomCompetence(): Competence {
    return Competence(
        competenceCounter++,
        getRandomWord(),
    )
}

/**
 * Generate a random list of keyword from [FirstProposal] by retrieving random word.
 *
 * @param dataSetSize Size of the data set of [FirstProposal] that you wanted to generate.
 *
 * @return The randomly generated list of keywords as a [List] of [String].
 *
 * @see getRandomWord
 */
private fun getRandomKeyWords(dataSetSize: Int): List<String> {
    val result = mutableSetOf<String>() // Prevent from adding the same word multiple times
    for(i in 0..(0.2*dataSetSize).toInt()) {
        result.add(getRandomWord())
    }
    return result.toList()
}

/**
 * Retrieve a random word from Lorem Ipsum.
 *
 * @return The randomly generated word as [String].
 *
 * @see getRandomInteger
 */
private fun getRandomWord(): String {
    return words[getRandomInteger(max = words.size - 1)]
}

/**
 * Generate a random [Int] value.
 *
 * @param min The minimal value accepted for the int.
 * @param max The maximal value accepted for the int.
 *
 * @return The randomly generated int value.
 *
 * @see IntRange.random
 */
private fun getRandomInteger(min: Int = 0, max: Int): Int {
    return (min..max).random()
}

/**
 * Generate a random [Long] value.
 *
 * @param min The minimal value accepted for the long.
 * @param max The maximal value accepted for the long.
 *
 * @return The randomly generated long value.
 *
 * @see LongRange.random
 */
private fun getRandomLong(min: Long, max: Long): Long {
    return (min..max).random()
}

/**
 * Generate a random [Boolean] value.
 *
 * @return The boolean value.
 *
 * @see getRandomInteger
 */
private fun getRandomBoolean(): Boolean {
    return (getRandomInteger(max = 1) == 1)
}


/**
 * Generate a random [Date] object.
 * This date would always be before the given date or, if non date was transmitted, before [defaultAfterDate].
 *
 * @param minDate The minimal accepted date.
 *
 * @return The randomly generated date.
 *
 * @see getRandomLong
 * @see defaultAfterDate
 */
private fun getRandomDate(minDate: Date = defaultAfterDate): Date {
    val now = Calendar.getInstance().time
    // Remove a random number of ms to now
    return Date(now.time - getRandomLong(minDate.time, now.time))
}

/**
 * Generate a sentence by picking a random number of words from Lorem Ipsum.
 * The sentence should measure between 20 and 40 words.
 *
 * @return A sentence as [String].
 *
 * @see getRandomInteger
 */
private fun getRandomSentences(): String {
    var sentence = ""
    for(i in 0..getRandomInteger(20, 40)) {
        sentence += "${getRandomWord()} "
    }
    val sentenceSize = sentence.length
    sentence.replaceRange(sentenceSize-2 until sentenceSize, ".")

    return sentence
}

/**
 * Generate a random [Float] number.
 * The min of the float will always be 0.
 *
 * @param max Maximal value of the float number.
 *
 * @return A randomly generated float number.
 */
private fun getRandomFloat(max: Float): Float {
    return random.nextFloat() * max
}

