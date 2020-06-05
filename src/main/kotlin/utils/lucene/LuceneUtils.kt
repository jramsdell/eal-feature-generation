package utils.lucene

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.*
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.FSDirectory
import utils.AnalyzerFunctions
import java.lang.IllegalStateException
import java.nio.file.Paths

//// Retrieves an index searcher (I use this everywhere so might as well put it here)
fun getIndexSearcher(indexLocation: String): IndexSearcher {
    val indexPath = Paths.get(indexLocation)
    val indexDir = FSDirectory.open(indexPath)
    val indexReader = DirectoryReader.open(indexDir)
    return IndexSearcher(indexReader)
}


fun getIndexWriter(indexLocation: String, mode: IndexWriterConfig.OpenMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND): IndexWriter {
    val indexPath = Paths.get(indexLocation)
    val indexDir = FSDirectory.open(indexPath)
    val conf = IndexWriterConfig(StandardAnalyzer())
        .apply { openMode = mode }
    return IndexWriter(indexDir, conf)
}



fun TopDocs.docs(indexSearcher: IndexSearcher): Sequence<Pair<Document, Int>> =
        scoreDocs.asSequence().map { scoreDoc ->  indexSearcher.doc(scoreDoc.doc) to scoreDoc.doc }

fun Document.getOrDefault(field: String, default: String = "") =
         get(field) ?: default
//    try { get(field) ?: default }
//    catch (e: IllegalStateException) { default }

fun Document.getValuesOrDefault(field: String) = getValues(field).toList()
//        try { getValues(field).toList() }
//        catch (e: IllegalStateException) { emptyList<String>() }

fun IndexSearcher.explainScore(query: Query, doc: Int) = explain(query, doc).value.toDouble()

fun IndexSearcher.searchFirstOrNull(query: Query) =
        search(query, 1)
            .scoreDocs
            .firstOrNull()

//inline fun<reified A: IndexType> IndexSearcher.getDocumentByField(id: String): IndexDoc<A>? {
//    val field = when (A::class.java) {
//         IndexType.ENTITY::class.java -> IndexFields.FIELD_NAME.field
//        IndexType.PARAGRAPH::class.java -> IndexFields.FIELD_PID.field
//        IndexType.SECTION::class.java -> IndexFields.FIELD_SECTION_ID.field
//        else -> "text"
//    }
//    val q = AnalyzerFunctions.createQuery(id, field = field)
//    val sc = search(q, 1).scoreDocs.firstOrNull()
//    return sc?.let { IndexDoc(doc(sc.doc)) }
//}
//
//inline fun<reified A: IndexType> IndexSearcher.getIndexDoc(id: Int) = IndexDoc<A>(doc(id))

