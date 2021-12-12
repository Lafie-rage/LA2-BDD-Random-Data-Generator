import data.common.model.Competence
import data.convertKeywordsToPropositions
import data.first.model.FirstProposition
import data.second.model.KeyWord
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future

private const val LOREM_IPSUM_PATH = "./src/main/resources/lorem_ipsum.txt"
private const val DEFAULT_DATE: String = "01/01/2000 00:00:00"
private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
private val defaultBeforeDate = dateFormat.parse(DEFAULT_DATE)
private val regexDelimiter = Regex("(([.]*[,]*[ ]|\n)+|[.])")
private val words = File(LOREM_IPSUM_PATH).readText().split(regexDelimiter).filter { it.isNotBlank() }
private val random = Random()


fun main(args: Array<String>) {
    if(args.isEmpty() || args[0].toIntOrNull() == null) {
        println("Please enter the size of the data set that you want to generate as an integer")
        return
    }

    val dataSetSize = args[0].toInt()
    val numberCoresAvailable = Runtime.getRuntime().availableProcessors()
    val executor = Executors.newFixedThreadPool(numberCoresAvailable)

    val dataSetSizeForEachThread = dataSetSize/numberCoresAvailable

    println("There's $numberCoresAvailable cores available.")
    println("Spreading generation on your different cores...")

    val globalProposition = mutableListOf<FirstProposition>()
    val globalKeyWords = mutableListOf<KeyWord>()
    val results = mutableListOf<Future<Pair<List<FirstProposition>, List<KeyWord>>>>()
    for(threadIndex in 0 until numberCoresAvailable) {

        val result = executor.submit<Pair<List<FirstProposition>, List<KeyWord>>> {
            val propositions = mutableListOf<FirstProposition>()

            // Generate a random data set for each defined models.
            // The size of the data set depends on the value given as argument when calling the program.
            val begin = System.nanoTime()
            println("Generation started on thread $threadIndex")
            for(i in 0 until dataSetSizeForEachThread) {
                propositions.add(getRandomProposition(i + (threadIndex * dataSetSizeForEachThread), dataSetSize))
            }
            println("Generation done on thread $threadIndex")
            val end = System.nanoTime()
            println("First mode generation done after ${(end - begin) / 1_000_000} milliseconds.")

            println("Converting first model to second one...")
            val keyWords = convertKeywordsToPropositions(propositions)
            println("Conversion done.")

            return@submit Pair(propositions, keyWords)
        }
        results.add(result)
    }

    results.forEach { future ->
        val result = future.get()
        globalProposition.addAll(result.first)
        globalKeyWords.addAll(result.second)
    }

    println("First model :")
//    globalProposition.forEach {
//        println(it)
//    }
    println("Size : ${globalProposition.size}")


    println("Second model :")
//    globalKeyWords.forEach { keyWord ->
//        print("Keyword : ${keyWord.keyWord}, [")
//        keyWord.propositions.forEach { proposition ->
//            print("${proposition.id}, ")
//        }
//        println("]")
//    }
    println("Size : ${globalKeyWords.size}")
}

private fun getRandomProposition(id: Int, dataSetSize: Int): FirstProposition {
    val beginDate = getRandomDate()
    return FirstProposition(
        id,
        getRandomInteger(0, dataSetSize),
        beginDate,
        getRandomDate(beginDate),
        getRandomFloat(dataSetSize.toFloat()),
        getRandomBoolean(),
        getRandomBoolean(),
        getRandomWord(),
        getRandomSentences(),
        getRandomCompetence(dataSetSize),
        getRandomKeyWords(dataSetSize),
    )
}

private fun getRandomCompetence(dataSetSize: Int): Competence {
    return Competence(
        getRandomInteger(0, dataSetSize),
        getRandomWord(),
    )
}

private fun getRandomKeyWords(dataSetSize: Int): List<String> {
    val result = mutableSetOf<String>() // Prevent from adding the same word multiple times
    for(i in 0..(0.2*dataSetSize).toInt()) {
        result.add(getRandomWord())
    }
    return result.toList()
}

private fun getRandomWord(): String {
    return words[getRandomInteger(0, words.size - 1)]
}

private fun getRandomInteger(min: Int, max: Int): Int {
    return (min..max).random()
}

private fun getRandomBoolean(): Boolean {
    return (getRandomInteger(0, 1) == 1)
}

private fun getRandomDate(before: Date = defaultBeforeDate): Date {
    return Calendar.getInstance().time
}

/**
 * Generate a sentence
 */
private fun getRandomSentences(): String {
    var sentence = ""
    for(i in 20..40) {
        sentence += "${getRandomWord()} "
    }
    val sentenceSize = sentence.length
    sentence.replaceRange(sentenceSize-2 until sentenceSize, ".")

    return sentence
}

/**
 * Generate a random [Float] number.
 * The min of the float will always be 0
 *
 * @param max Maximal value of the float.
 */
private fun getRandomFloat(max: Float): Float {
    return random.nextFloat() * max
}

