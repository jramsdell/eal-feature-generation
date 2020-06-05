package features.containers

import indexing.StatType
import indexing.StatType.*
import utils.AnalyzerFunctions
import java.io.File


data class CorpusStatsContainer(
    val statMap: Map<String, Map<String, Double>>,
    val nParagraphs: Int,
    val nSections: Int,
    val averageContextTextLength: Double,
    val averageContextEntityLength: Double,
    val averageSectionTextLength: Double,
    val averageSectionEntityLength: Double,
    val averageSectionHeadingLength: Double
) {
    fun retrieveNDocs(t: StatType) = if (t.isAspect) nSections else nParagraphs

    fun retrieveFreqMap(t: StatType) = statMap[t.field]!!

    fun retrieveAverageDocLength(t: StatType) =
        when(t) {
            CONTEXT_TEXT -> averageContextTextLength
            CONTEXT_ENTITY -> averageContextEntityLength
            SECTION_TEXT -> averageSectionTextLength
            SECTION_HEADER -> averageSectionHeadingLength
            SECTION_ENTITY -> averageSectionEntityLength
        }

    fun getTfIdfVector(text: String, type: StatType): Map<String, Double> {
        val freqMap = retrieveFreqMap(type)
        val nDocs = retrieveNDocs(type)

        val termFreqs = AnalyzerFunctions.createLemmatizedSentence(text)
            .filter { it in freqMap }
            .groupingBy { it }
            .eachCount()

//        val docLength = termFreqs.values.sum().toDouble()
        val docLengthSmooth = termFreqs.values.sumByDouble { it + 1.0 }

        return termFreqs.map { (token, freq) ->
            token to getIdf2(token, freqMap, nDocs) * Math.log(1.0 + ((freq + 1.0) / docLengthSmooth))
        }.toMap()
    }

    fun getBM25Vector(text: String, type: StatType): Map<String, Double> {
        val freqMap = retrieveFreqMap(type)
        val nDocs = retrieveNDocs(type)
        val avgDocLen = retrieveAverageDocLength(type)

        val termFreqs = AnalyzerFunctions.createLemmatizedSentence(text)
            .filter { it in freqMap }
            .groupingBy { it }
            .eachCount()

//        val docLength = termFreqs.values.sum().toDouble()
        val docLengthSmooth = termFreqs.values.sumByDouble { it + 1.0 }

        return termFreqs.map { (token, freq) ->
            val idf = getIdf2(token, freqMap, nDocs)
            val tf = Math.log(1.0 + (freq + 1.0) / docLengthSmooth)
            val k1 = 2.0
            val b = 0.75
            val bm25 = idf * (tf * (k1 + 1.0)).div(tf + (k1 * (1.0 - b + b * (docLengthSmooth / avgDocLen))))
            token to bm25
        }.toMap()
    }

    fun getIdf(term: String, freqMap: Map<String, Double>, nDocs: Int) =
        1.0 + Math.log(nDocs / (1.0 + (freqMap[term] ?: error("Should exist in freqMap"))))

    fun getIdf2(term: String, freqMap: Map<String, Double>, nDocs: Int): Double {
        val fn = freqMap[term]!!
        return Math.log((nDocs - fn + 0.5) / (fn + 0.5))
    }

    companion object {
        fun fromStatsCsv(statLoc: String): CorpusStatsContainer {
            val reader = File(statLoc).bufferedReader()
            val sMap = HashMap<String, HashMap<String, Double>>()

            val statMap: Map<String, Map<String, Double>>
            val nParagraphs: Int
            val nSections: Int
            val averageContextTextLength: Double
            val averageContextEntityLength: Double
            val averageSectionTextLength: Double
            val averageSectionEntityLength: Double
            val averageSectionHeadingLength: Double

            reader.readLine().let { line ->
                val (paragraph, section) = line.trimEnd().split("\t")
                nParagraphs = paragraph.toInt()
                nSections = section.toInt()
            }

            reader.readLine().let { line ->
                val (cTextLen, cEntityLen, sTextLen, sEntityLen, sHeadingLen) = line.trimEnd().split("\t")
                averageContextTextLength = cTextLen.toDouble()
                averageContextEntityLength = cEntityLen.toDouble()
                averageSectionTextLength = sTextLen.toDouble()
                averageSectionEntityLength = sEntityLen.toDouble()
                averageSectionHeadingLength = sHeadingLen.toDouble()
            }

            reader.readLines().forEach { line ->
                val (type, token, freq) = line.trimEnd().split("\t")
                sMap.computeIfAbsent(type) { HashMap() }[token] = freq.toDouble()
            }


            return CorpusStatsContainer(
                statMap = sMap,
                averageSectionEntityLength = averageSectionEntityLength,
                averageContextEntityLength = averageContextEntityLength,
                averageContextTextLength = averageContextTextLength,
                averageSectionHeadingLength = averageSectionHeadingLength,
                averageSectionTextLength = averageSectionTextLength,
                nParagraphs = nParagraphs,
                nSections = nSections
            )

        }
    }
}

