package parsing

//import com.beust.klaxon.*
//import edu.unh.cs.treccar_v2.Data
//import edu.unh.cs.treccar_v2.read_data.DeserializeData
//import lucene.indexer.containers.AspectCandidate
//import lucene.indexer.containers.AspectEntry
//import lucene.indexer.containers.AspectPair
//import managers.AspectQuery
//import org.threadly.concurrent.collections.ConcurrentArrayList
//import utils.misc.FIXED_FEDE_DATASET
//import utils.misc.FIXED_LUCENE_NEW
//import utils.misc.FIXED_NEW_DATASET
//import utils.parallel.asIterable
//import utils.parallel.forEachParallelQ
//import utils.parallel.pmap
//import utils.stats.countDuplicates
//import java.io.BufferedWriter
//import java.io.File
//import java.lang.Exception
//import kotlin.streams.toList


//class AuxillaryCandidateRetriever() {
//
//    fun loadCandidatesFromJson(jsonLoc: String): Map<String, AspectPair> {
//        val k = Klaxon()
//        @Suppress("UNCHECKED_CAST")
//        val results = File(jsonLoc).bufferedReader()
//            .lines()
//            .toList()
//            .pmap { line ->
//
//                 val p = k.parse<AspectPair>(line)!!
//
//                p.id to p
//            }
//            .toList()
//            .toMap()
//        return results
//    }
//
//    companion object {
//        fun writeCandidatesUsingAspectList(entries: Sequence<AspectEntry>, cborLocs: List<String>, annoLocs: List<String>, outLoc: String) {
//            val mmaap = HashMap<String, ArrayList<String>>()
//            annoLocs.forEach { annoLoc ->
//                File(annoLoc).bufferedReader().use { reader ->
//                    reader.forEachLine { line ->
//                        val (mentionId, entity, _) = line.split("\t")
//                        mmaap.computeIfAbsent(mentionId.replace("enwiki:", "")) { ArrayList<String>() }.add(entity)
//                    }
//                }
//            }
//
//
//
//            val entities = entries
////                .asSequence()
//                .filter { entry -> entry.entity.replace("enwiki:", "") in mmaap }
////                .apply { println("SIZE: $size") }
//                .asIterable()
//                .chunked(500)
//                .pmap { it.map { entry ->
//                    entry.paraContext.entities = (entry.paraContext.entities + mmaap[entry.entity.replace("enwiki:", "")]!!.toList()).distinct()
//
//                    // todo : Remove dirty stopgap measure
////                    println(entry.paraContext.entities.size)
////                    entry.paraContext.entities = entry.paraContext.entities.take(5)
//                    entry.paraContext.entities
//                    .filter { it != entry.correctAspectId }
//                    .map { entity ->
//                        entity.replace("enwiki:", "").replace("%20", "_")
////                }.asSequence()
//                    }
//                }.flatten()
//
//            }.flatten().toSet()
//
//            parseCandidatesToJson(entities, cborLocs, outLoc)
//        }
//
//        fun parseCandidatesToJson(entities: Set<String>, cborLocs: List<String>, outLoc: String) {
//            val writer = File(outLoc).bufferedWriter()
//            val results = cborLocs.forEach { loc ->
//                retrieveCandidates(entities, loc, writer)
//            }
//            writer.close()
//
//
////            File(outLoc).bufferedWriter().use { out ->
////                json { JsonObject(results) }.toJsonString()
////            }
//
//        }
//
//
//        fun retrieveCandidates(entities: Set<String>, cborLoc: String, writer: BufferedWriter): List<Pair<String, List<AspectCandidate>>> {
//            val results = ConcurrentArrayList<Pair<String, List<AspectCandidate>>>()
//            val badWords = listOf(
//                "see also",
//                "reference",
//                "references",
//                "notes",
//                "further reading",
//                "external links",
//                "bibliography"
//            )
//
//            File(cborLoc).inputStream().buffered().use { f ->
//                DeserializeData.iterableAnnotations(f)
//                    .forEachParallelQ(10) { page ->
//
//                            val isCorrectId = page.pageId
//                                .replace("enwiki:", "")
//                                .replace("%20", "_")
//                                .let { pageId -> pageId in entities }
//
//
//                            if (isCorrectId) {
//                                val result = page.pageId to page.childSections
//                                    .filter { section -> section.heading.toLowerCase() !in badWords }
//                                    .map { section ->
//                                        val (entities, text) = getSectionTextAndEntities(section)
//                                        AspectCandidate(
//                                            id = section.headingId,
//                                            header = section.heading,
//                                            content = text.joinToString("\n"),
//                                            entities = entities.toList()
//                                        )
//                                    }
//
//                                writer.write(
//                                    json {
//                                        obj("id" to result.first,
//                                            "candidates" to JsonArray(result.second.map { it.toJson() }))
//                                    }.toJsonString().replace("\n", " ") + "\n"
//                                )
//                            }
//                        }
//            }
//            return results.toList()
//        }
//
//
//        fun getSectionTextAndEntities(s: Data.PageSkeleton): Pair<Set<String>, List<String>>  =
//            when(s) {
//                is Data.Section -> {
//                    s.children.map { child -> getSectionTextAndEntities(child) }
//                        .fold(emptySet<String>() to emptyList<String>()) { cur, next ->
//                            cur.first + next.first to cur.second + next.second
//                        }
//                }
//                is Data.Para -> {
//                    s.paragraph.entitiesOnly.toSet() to listOf(s.paragraph.textOnly)
//                }
//                else -> {  emptySet<String>() to emptyList<String>() }
//            }
//    }
//
//
//
//
//}
//
//fun runParse() {
//    val cbors = listOf(
//        "/home/jsc57/data/unprocessedAllButBenchmark.cbor/unprocessedAllButBenchmark.cbor",
//        "/home/jsc57/data/test200/test200-train/train.pages.cbor",
//        "/home/jsc57/data/benchmark/benchmarkY1/benchmarkY1-train/train.pages.cbor",
//        "/home/jsc57/data/benchmark/benchmarkY1/benchmarkY1-test/test.pages.cbor"
//    )
//
////    val ap = AspectQuery(FIXED_LUCENE_NEW, FIXED_FEDE_DATASET)
////    val ap2 = AspectQuery(FIXED_LUCENE_NEW, FIXED_NEW_DATASET)
////    val entries = AspectQuery.parseJson(FIXED_FEDE_DATASET) + AspectQuery.parseJson(FIXED_NEW_DATASET)
//    val entries = AspectQuery.parseJsonSequence(FIXED_FEDE_DATASET) + AspectQuery.parseJsonSequence(FIXED_NEW_DATASET)
////    val entries = AspectQuery.parseJsonSequence(FIXED_FEDE_DATASET)
////    val entries = AspectQuery.parseJson(FIXED_NEW_DATASET)
//
////    val entries = ap.entries + ap2.entries
//
//    AuxillaryCandidateRetriever.writeCandidatesUsingAspectList(entries, cbors,
//        listOf(
//            "/home/jsc57/data/shared/para-ent.tsv",
//            "/home/jsc57/data/shared/new-dataset-para-ent.tsv"
//        ), "wee.json")
//}


