package features.managers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import features.bindings.FeatureBinding
import features.containers.CorpusStatsContainer
import features.containers.FeatureContainer
import json.containers.Aspect
import json.containers.AspectLinkExample
import org.apache.lucene.search.BooleanQuery
import org.nd4j.linalg.primitives.AtomicDouble

//import parsing.AuxillaryCandidateRetriever

import utils.parallel.forEachParallelRestricted
import utils.parallel.pmapRestricted
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random


class AspectQuery(jsonLoc: String, statLoc: String) {
//    val searcher = getIndexSearcher(indexLoc)
//    val searcher = getIndexSearcher(indexLoc)
    val stats = CorpusStatsContainer.fromStatsCsv(statLoc)


    //    val entries = parseJsonSequence(jsonLoc)
    val entries = parseJson(jsonLoc)
//        .filter { entry -> entry.entity.replace("enwiki:", "") in mmaap }
//        .onEach { entry -> entry.paraContext.entities = (entry.paraContext.entities + mmaap[entry.entity.replace("enwiki:", "")]!!.toList()).distinct() }
        .toList().shuffled(Random(1)).take(1000)
//    val a = AuxillaryCandidateRetriever()
//        .run { loadCandidatesFromJson("wee.json") }
//    a.loadCandidatesFromJson("wee.json")

    var debugPrintScores = false

    init {
        BooleanQuery.setMaxClauseCount(80000)
    }




    fun writeFeature(fb: FeatureBinding, outName: String = "fede") {
        val scorer = fb.binding
        val results = ConcurrentHashMap<FeatureContainer, List<Double>>()

        println("Entries: ${entries.size}")
        val counter = AtomicInteger(0)

        entries.forEachParallelRestricted(nThreads = 10) { entry: AspectLinkExample ->
            val fc = FeatureContainer(entry, stats)
            val candidateScores = scorer(fc)
            results[fc] = candidateScores
        }
        val curCounter = counter.incrementAndGet()
        if (curCounter % 1000 == 0) {
            println("{} out of {}".format(curCounter, entries.size))
        }

        if (!File("features/$outName").exists()) {
            File("features/$outName").mkdirs()
        }

        val out = File("features/$outName/${fb::class.simpleName}").bufferedWriter()

        results.entries.sortedByDescending { it.key.entry.id }
            .forEach { (fc,scores) ->
                val id = fc.entry.id
                fc.entry.candidateAspects
                    .distinctBy { it.aspectId }
                    .zip(scores)
                    .sortedByDescending { it.second }
                    .forEachIndexed { index, (candidate, score) ->
                    out.write("$id 0 ${candidate.aspectId} ${index + 1} $score run\n")
                }
            }

        out.close()
    }


    fun writeQrel(outName: String = "new-dataset") {
        if (!File("qrels/").exists()) {
            File("qrels/").mkdir()
        }

        val out = File("qrels/$outName.qrel").bufferedWriter()

        entries.forEach { entry ->
            val relevant = entry.trueAspect
            val qid = entry.id
            entry.candidateAspects
                .distinctBy { it.aspectId }
                .forEach { candidate ->
                out.write("$qid 0 ${candidate.aspectId} ${if (candidate.aspectId == relevant) 1 else 0}\n")
            }
        }

        out.close()


    }


    /**
     * Scores each entity aspect using a given feature.
     */
    fun run(fb: FeatureBinding): String {
        val scorer = fb.binding
        val precisionTotal = AtomicDouble(0.0)
        val mapTotal = AtomicDouble(0.0)
        val count = AtomicDouble(0.0)

        val histogram = ConcurrentHashMap<Int, List<Double>>()

        entries.forEachParallelRestricted(nThreads = 10) { entry: AspectLinkExample ->
            val fc = FeatureContainer(entry, stats)
            val candidateScores = scorer(fc)

            // DEBUG: check scores
            if (debugPrintScores)
                println(candidateScores)

            // Get best candidate based on feature
            val bestIndex = candidateScores
                .withIndex()
                .maxBy { (_, value) -> value }!!
                .index
            val bestId = entry.candidateAspects[bestIndex].aspectId

            // Update Precision@1 and MAP based on the top-ranked aspect
            count.addAndGet(1.0)
            val correct = if (bestId == entry.trueAspect) 1.0 else 0.0
            val mapScore = calculateMap(entry.trueAspect, candidateScores, entry.candidateAspects)
            val nCandidates = entry.candidateAspects.size

            precisionTotal.addAndGet(correct)
            mapTotal.addAndGet(mapScore)
        }

        histogram.entries.sortedByDescending { it.key }
            .map { it.key to it.value.average() }
            .forEach { (k,v) -> println("$k: $v") }

        // Calculate final precision@1 for feature based on dataset
        val precisionAtOne = precisionTotal.get() / count.get()
        val map = mapTotal.get() / count.get()
        val result =  "${fb::class.simpleName} & $precisionAtOne & $map\\\\"
        println(result)
        return result
    }


    fun calculateMap(correctId: String, candidateScores: List<Double>, candidates: List<Aspect>): Double {
        val topCandidates = candidateScores.zip(candidates)
            .sortedByDescending { it.first }
            .map { it.second }

        var nom = 0.0
        var denom = 0.0
        var total = 0.0

        topCandidates.forEach { candidate ->
            denom += 1.0
            if (candidate.aspectId == correctId) {
                nom += 1.0
                total += nom / denom
            }
        }

        return total
    }




    companion object {
        /**
         * Parses jsonl in parallel (contains entity aspect examples)
         */
        fun parseJson(loc: String): List<AspectLinkExample> =
            File(loc)
                .readLines()
                .pmapRestricted(10) { line ->
                    val mapper = jacksonObjectMapper()
                    mapper.readValue<AspectLinkExample>(line)
                }
//                .also { entries ->
//                    val seen = HashMap<String, Int>()
//                    entries.forEach {  entry ->
//                        if (entry.id in seen) {
//                            seen[entry.id] = seen[entry.id]!! + 1
//                            entry.id = entry.id + "_" + seen[entry.id].toString()
//                        } else {
//                            seen[entry.id] = 1
//                        }
//                    }
    }
}




// 0.144 vs. 0.037
