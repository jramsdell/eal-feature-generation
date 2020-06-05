package indexing

import edu.unh.cs.treccar_v2.Data
import java.io.File
import edu.unh.cs.treccar_v2.read_data.DeserializeData.iterableAnnotations
import utils.AnalyzerFunctions
import utils.parallel.forEachParallelQ
import utils.parallel.forEachParallelRestricted
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class CorpusStatIndexer(val indexFolderLoc: String, val pageCborLoc: String, val nThreads: Int = 10) {
    val paraParsers = ArrayList<(Data.Para) -> Unit>()
    val sectionParsers = ArrayList<(Data.Section) -> Unit>()

    val freqMap = ConcurrentHashMap<String, ConcurrentHashMap<String, Double>>()
    val paraCounter = AtomicInteger()
    val sectionCounter = AtomicInteger()
    val contextTextLengthCounter = AtomicInteger()
    val contextEntityLengthCounter = AtomicInteger()
    val sectionTextLengthCounter = AtomicInteger()
    val sectionEntityLengthCounter = AtomicInteger()
    val sectionHeadingLengthCounter = AtomicInteger()

    init {
        if (!File(indexFolderLoc).exists()) {
            File(indexFolderLoc).mkdirs()
        }
    }

    fun foldOverChildren(children: List<Data.PageSkeleton>): Unit =
        children.forEach { child ->
            when (child) {
                is Data.Para -> parseParagraph(child.paragraph)
                is Data.Section -> {
                    parseSection(child)
                    foldOverChildren(child.children)
                }
            }
        }

    fun updateFreqMap(token: String, freqEntry: String) =
        freqMap.computeIfAbsent(token) { ConcurrentHashMap() }
            .compute(freqEntry) { _: String, cur: Double? -> (cur ?: 0.0) + 1.0 }

    fun parseParagraph(p: Data.Paragraph) {
        paraCounter.incrementAndGet()
        val text = p.textOnly
        val entities = p.bodies.filterIsInstance<Data.ParaLink>()
            .map { it.page.toLowerCase() }
            .distinct()


        AnalyzerFunctions.createLemmatizedParagraph(text.toLowerCase())
            .distinct()
            .also { contextTextLengthCounter.addAndGet(it.size)}
            .forEach { token -> updateFreqMap(token, "contextText") }

        entities.flatMap(AnalyzerFunctions::createLemmatizedParagraph)
            .distinct()
            .also { contextEntityLengthCounter.addAndGet(it.size)}
            .forEach { token -> updateFreqMap(token, "contextEntity") }
    }

    fun parseSection(s: Data.Section) {
        sectionCounter.incrementAndGet()
        getSectionText(s)
            .distinct()
            .also { sectionTextLengthCounter.addAndGet(it.size)}
            .forEach { token -> updateFreqMap(token, "sectionText") }

        getSectionEntities(s)
            .distinct()
            .also { sectionEntityLengthCounter.addAndGet(it.size)}
            .forEach { token -> updateFreqMap(token, "sectionEntities") }

        AnalyzerFunctions.createLemmatizedParagraph(s.heading.toLowerCase())
            .distinct()
            .also { sectionHeadingLengthCounter.addAndGet(it.size)}
            .forEach { token -> updateFreqMap(token, "sectionHeader") }
    }

    fun getSectionText(s: Data.Section): List<String> =
        s.children.flatMap { child ->
            when(child) {
                is Data.Section -> { AnalyzerFunctions.createLemmatizedParagraph(child.heading.toLowerCase()) + getSectionText(child)}
                is Data.Para -> { AnalyzerFunctions.createLemmatizedParagraph(child.paragraph.textOnly.toLowerCase()) }
                else -> emptyList()
            }
        }

    fun getSectionEntities(s: Data.Section): List<String> =
        s.children.flatMap { child ->
            when(child) {
                is Data.Section -> { getSectionEntities(child)}
                is Data.Para -> {
                            child.paragraph.bodies.filterIsInstance<Data.ParaLink>().flatMap { link ->
                                AnalyzerFunctions.createLemmatizedParagraph(link.page.toLowerCase())
                            }
                }
                else -> emptyList()
            }
        }

    fun writeFrequencies() {
        val writer = File("${indexFolderLoc}/corpus_stats.csv").bufferedWriter()
        val nParagraphs = paraCounter.get()
        val nSections = sectionCounter.get()
        val lengthStats = listOf(
            contextTextLengthCounter.get().toDouble() / nParagraphs,
            contextEntityLengthCounter.get().toDouble() / nParagraphs,
            sectionTextLengthCounter.get().toDouble() / nSections,
            sectionEntityLengthCounter.get().toDouble() / nSections,
            sectionHeadingLengthCounter.get().toDouble() / nSections
        )

        writer.write("$nParagraphs\t$nSections\n")
        writer.write(lengthStats.joinToString("\t") + "\n")

        freqMap.flatMap { (token, freqTypes) ->
            freqTypes.map { (type, freq) ->  type to (token to freq) }
        }.groupBy { (type, _) -> type }
            .forEach { (type, results) ->
                results.forEach { (_, result) ->
                    val (token, freq) = result
                    writer.write("$type\t$token\t$freq\n")
                }
            }
        writer.close()
    }



    fun run() {
        val counter = AtomicInteger()
        iterableAnnotations(File(pageCborLoc).inputStream())
            .asSequence()
            .take(80000)
            .withIndex()
            .forEachParallelQ(qSize = 50, nThreads = nThreads) { (index, page) ->
                val curCounter = counter.incrementAndGet()
                if (curCounter % 1000 == 0) {
                    println(curCounter)
                }
                if (index % 4 == 0) {
                    foldOverChildren(page.skeleton)
                }
            }
        writeFrequencies()
    }
}