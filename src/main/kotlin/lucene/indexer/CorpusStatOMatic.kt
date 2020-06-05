package lucene.indexer

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.Annotation
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.apache.lucene.index.Term
import utils.lucene.getIndexSearcher
import edu.stanford.nlp.process.PTBTokenizer
import edu.stanford.nlp.simple.Document
import edu.stanford.nlp.simple.Sentence
import org.apache.lucene.search.IndexSearcher
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object CorpusStatOMatic {

    val memoizedAverageFieldLengths = ConcurrentHashMap<String, Double>()


//    fun termIDF(searcher: IndexSearcher, term: String, field: String): Double {
//        val nDocs = searcher.indexReader.totalTermFreq(Term(field, term))
//        val totalDocs = searcher.indexReader.numDocs()
//        return Math.log((totalDocs / (nDocs + 1)).toDouble())
//    }

    fun termIDF(searcher: IndexSearcher, term: String, field: String): Double {
        val nDocs = searcher.indexReader.totalTermFreq(Term(field, term))
        val totalDocs = searcher.indexReader.numDocs()
        val corpusTermFreq = searcher.indexReader.docFreq(Term(field, term))
        return Math.log((nDocs - corpusTermFreq + 0.5) / (corpusTermFreq + 0.5) )
    }

    fun getAverageDocLength(searcher: IndexSearcher, field: String): Double {
        return memoizedAverageFieldLengths.computeIfAbsent(field) { key: String ->
            val stats = searcher.collectionStatistics(field)
//            stats.sumTotalTermFreq() / stats.docCount().toDouble()
            (0 until searcher.indexReader.numDocs()).sumBy { docId ->
                val doc = searcher.doc(docId)
                doc.get(field).toLowerCase().trimEnd().split(" ").size
            }.toDouble().run { this / searcher.indexReader.maxDoc() }
        }
    }


    fun bm25Term(searcher: IndexSearcher, term: String, field: String, termFreq: Double,
                 docLength: Int, k: Double, b: Double): Double {
        val idf = termIDF(searcher, term, field)
        val numerator = termFreq * (k + 1.0)
        val denom = idf + k * (1 - b + b * (docLength / getAverageDocLength(searcher, field)))
        return idf * (numerator / denom)
    }

    fun createBm25Vector(searcher: IndexSearcher, text: String, field: String): Map<String, Double> {
        val tokens = text.toLowerCase().trimEnd().split(" ")
        if (tokens.isEmpty())
            return emptyMap()

        return tokens
            .groupingBy { it }
            .eachCount()
            .mapValues { (token,freq) ->
                val termFreq = freq / tokens.size.toDouble()
                val idf = termIDF(searcher, token, field)
                idf * bm25Term(searcher, token, field, termFreq, tokens.size, k = 2.0, b = 0.75)
            }

    }

    fun createTermFrequencyVector(searcher: IndexSearcher, text: String, field: String): Map<String, Double> {
        val tokens = text.toLowerCase().trimEnd().split(" ")
        if (tokens.isEmpty())
            return emptyMap()

        return tokens
            .groupingBy { it }
            .eachCount()
            .mapValues { (token,freq) ->
                val termFreq = freq / tokens.size.toDouble()
                Math.log(1.0 + termFreq) * termIDF(searcher, token, field)
            }
    }
}


