package lucene.indexer
//
//import org.apache.lucene.document.Document
//import org.apache.lucene.document.Field
//import org.apache.lucene.document.StringField
//import org.apache.lucene.document.TextField
//import utils.AnalyzerFunctions
//import utils.AnalyzerType
//import utils.lucene.getIndexWriter
//import java.io.File
//import kotlin.random.Random
//
//
//class CandidateIndexer() {
//
//    fun parseJson(locs: List<String>, outLoc: String) {
//        val writer = getIndexWriter(outLoc)
//
//        locs.forEach { loc ->
//            val jText = File(loc).readLines()
//            val k = Klaxon()
//
//            jText.forEach {
//                val entry = k.parse<AspectEntry>(it)!!
//
//                entry.aspectCandidates
//                    .shuffled(Random(1))
//                    .take(8000)
//                    .forEach { candidate ->
//                    val doc = Document()
//
////                val headerTokens = AnalyzerFunctions.createTokenList(candidate.header, AnalyzerType.ANALYZER_ENGLISH).joinToString(" ")
////                val contentTokens = AnalyzerFunctions.createTokenList(candidate.content, AnalyzerType.ANALYZER_ENGLISH).joinToString(" ")
////                val entityTokens = AnalyzerFunctions.createTokenList(candidate.entities.joinToString(" "), AnalyzerType.ANALYZER_ENGLISH).joinToString(" ")
//
//                    val headerTokens = AnalyzerFunctions.createLemmatizedSentence(candidate.header.toLowerCase().trimEnd())
//                        .joinToString(" ")
//                    val contentTokens = AnalyzerFunctions.createLemmatizedSentence(candidate.content.toLowerCase().trimEnd())
//                        .joinToString(" ")
//                    val entityTokens = candidate.entities.joinToString(" ")
//
//                    doc.add(TextField("header", headerTokens, Field.Store.YES))
//                    doc.add(TextField("content", contentTokens, Field.Store.YES))
//                    doc.add(TextField("entities", entityTokens, Field.Store.YES))
//                    doc.add(StringField("id", candidate.id, Field.Store.YES))
//
//                    writer.addDocument(doc)
//                }
//
//            }
//        }
//
//        writer.commit()
//        writer.close()
//    }
//
//
//}
//
