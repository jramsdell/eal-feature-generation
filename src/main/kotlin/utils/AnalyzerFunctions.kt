package utils

import edu.stanford.nlp.simple.Document
import edu.stanford.nlp.simple.Sentence
import lucene.indexer.CorpusStatOMatic
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import java.io.StringReader
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentHashMap

//private fun buildStopWords(): CharArraySet {
//    val stops = CharArraySet.copy(EnglishAnalyzer.getDefaultStopSet())
//     stops.addAll( File("resources/aggressive_stops.txt").readLines() )
//    return stops
//}


data class WeightedTermData(
        val weightedTerms: Map<String, Double>,
        val field: String,
        val addiitonalWeight: Double )

enum class AnalyzerType { ANALYZER_STANDARD, ANALYZER_ENGLISH, ANALYZER_ENGLISH_STOPPED,
    ANALYZER_STANDARD_STOPPED}

//private val disallowedCharacters = setOf( '!', '?', '-', '*', '$', '%', '^', '@', '#', '~', '/', '\'', '"',
//    '_", "+", "=", ">", "<")

private val stopWords = CorpusStatOMatic::class.java.classLoader.getResource("stopwords.txt")!!
    .readText()
    .split("\n")
    .toSet()


private fun filterPuncs(tokens: List<String>) = tokens.filter { token ->
    token.length > 2 && token !in stopWords && !token.matches(".*\\d.*".toRegex())
//    token.length > 2 && token !in stopWords
}

/**
 * Static Class: utils.AnalyzerFunctions
 * Desc: Contains a collection of tokenizing / lucene building functions used by other scripts.
 */
object AnalyzerFunctions {
    private val standardAnalyzer = StandardAnalyzer()
    private val englishAnalyzer = EnglishAnalyzer()
//    private val englishStopped = EnglishAnalyzer(buildStopWords())
    private val englishStopped = EnglishAnalyzer()
//    private val standardAnalyzerStopped = StandardAnalyzer(buildStopWords())
    private val standardAnalyzerStopped = StandardAnalyzer()

    fun createLemmatizedSentence(text: String): List<String> =
        try {
            Sentence(text).lemmas()
//                .flatMap { it.split(" ") }
                .run(::filterPuncs)
        } catch (e: IllegalStateException) { emptyList()}



    fun createLemmatizedParagraph(text: String): List<String> =
        Document(text).sentences()
            .flatMap { sentence -> sentence.lemmas()
//                .flatMap { it.split(" ") }
                .run(::filterPuncs) }



    /**
     * Class: createTokenSequence
     * Description: Given a lucene string, tokenizes it and returns a list of String tokens
     * @param analyzerType: Type of analyzer (english or standard)
     * @param useFiltering: If true, filter out numbers, enwiki: and other noise from lucene
     * @return Sequence<String>
     * @see AnalyzerType
     */
    fun createTokenList(query: String,
                        analyzerType: AnalyzerType = AnalyzerType.ANALYZER_STANDARD,
                        useFiltering: Boolean = false): List<String> {
        val analyzer = when (analyzerType) {
            AnalyzerType.ANALYZER_STANDARD -> standardAnalyzer
            AnalyzerType.ANALYZER_ENGLISH -> englishAnalyzer
            AnalyzerType.ANALYZER_ENGLISH_STOPPED -> englishStopped
            AnalyzerType.ANALYZER_STANDARD_STOPPED -> standardAnalyzerStopped
        }

        val finalQuery =
                    if (useFiltering) query.replace("enwiki:", "")
                        .replace("tqa:", "")
                        .replace("%20", " ")
                        .replace("/", " ")
                else query


        val tokens = ArrayList<String>()
        val tokenStream = analyzer.tokenStream("text", StringReader(finalQuery)).apply { reset() }
        while (tokenStream.incrementToken()) {
            tokens.add(tokenStream.getAttribute(CharTermAttribute::class.java).toString())
        }
        tokenStream.end()
        tokenStream.close()

        return tokens
    }



}