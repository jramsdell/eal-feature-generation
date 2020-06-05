package features

import features.containers.CandidateDocFieldType
import features.containers.AspectFieldType
import features.containers.ContextType
import features.containers.FeatureContainer
import indexing.StatType
import json.containers.EntityMention
import lucene.indexer.CorpusStatOMatic
//import managers.AspectQuery
import org.apache.commons.math3.stat.StatUtils
import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import utils.AnalyzerFunctions
import utils.AnalyzerType
import utils.nd4j.cosineSim

//import utils.misc.FIXED_FEDE_DATASET
//import utils.misc.FIXED_LUCENE_FEDE
//import utils.misc.FIXED_NEW_DATASET
//import utils.nd4j.combineNDArrays
//import utils.nd4j.toNDArray
//import utils.stats.normalize
import java.io.File
import java.lang.Double.sum
import java.lang.IllegalStateException
import kotlin.math.pow
import kotlin.random.Random

object BasicFeatures {
    val word2VecRef = lazy {
        //        Nd4j.factory().blas().maxThreads = 1
        WordVectorSerializer.readWord2VecModel(
//            File("/home/hcgs/data_science/data/GoogleNews-vectors-negative300-SLIM.bin.gz")
            File("/home/jsc57/projects/entity-aspect-linking-fedes-method/GoogleNews-vectors-negative300-SLIM.bin.gz")
//            File("/home/jsc57/data/embeddings/GoogleNews-vectors-negative300.bin")
        )
    }


    /**
     * Feature that scores candidates according to number of tokens
     */
    fun simpleHeuristicContentOverlap(fc: FeatureContainer,
                                      contextType: ContextType<String>,
                                      aspectFieldType: AspectFieldType<String>): List<Double> = with(fc) {
        val contextTerms = contextType.field(entry)
            .run(AnalyzerFunctions::createLemmatizedSentence)
            .toSet()

        entry.candidateAspects.map { candidate ->
            val candidateTerms = aspectFieldType.field(candidate)
                .run(AnalyzerFunctions::createLemmatizedSentence)
                .toSet()

            val nIntersect = contextTerms.intersect(candidateTerms).size.toDouble()
            val nUnion = contextTerms.union(candidateTerms).size.toDouble()
//            nIntersect / nUnion
            nIntersect.toDouble()
        }
    }



    /**
     * Feature that scores candidates according to number of tokens
     */
    fun simpleHeuristicEntityOverlap(fc: FeatureContainer,
                                      contextType: ContextType<List<EntityMention>>,
                                      aspectFieldType: AspectFieldType<List<EntityMention>>): List<Double> = with(fc) {
        val contextTerms = contextType.field(entry)
            .map { it.entityId }
            .toSet()

        entry.candidateAspects.map { candidate ->
            val candidateTerms = aspectFieldType.field(candidate)
                .map { it.entityId }
                .toSet()

            val nIntersect = contextTerms.intersect(candidateTerms).size.toDouble()
            val nUnion = contextTerms.union(candidateTerms).size.toDouble()
            nIntersect / nUnion
        }
    }

    /**
     * Feature that scores candidates according to number of tokens
     */
    fun simpleHeuristicSize(fc: FeatureContainer,
                            aspectFieldType: AspectFieldType<String>): List<Double> = with(fc) {
        entry.candidateAspects.map { candidate ->
            aspectFieldType.field(candidate)
                .run(AnalyzerFunctions::createLemmatizedSentence)
                .size.toDouble()
        }
    }

    fun tfidfOrBm25(fc: FeatureContainer,
                            contextType: ContextType<String>,
                            contextStatType: StatType,
                            aspectFieldType: AspectFieldType<String>,
                            aspectStatType: StatType,
                            isBM25: Boolean): List<Double> = with(fc) {

//        val vecMethod: (String, StatType) -> Map<String, Double> =
//            if(isBM25) fc.stats::getBM25Vector else fc.stats::getTfIdfVector
        val vecMethod =
            if(isBM25) fc.stats::getBM25Vector else fc.stats::getTfIdfVector

        val contextText = contextType.field(entry).toLowerCase()
        val contextVector = vecMethod(contextText, contextStatType)
            .run {
                val t = values.sumByDouble { it.pow(2.0) }
                mapValues { it.value / t }
            }

        entry.candidateAspects.map { candidate ->
            val aspectText = aspectFieldType.field(candidate).toLowerCase()
            val aspectVector = vecMethod(aspectText, aspectStatType)
                .run {
                    val t = values.sumByDouble { it.pow(2.0) }
                    mapValues { it.value / t }
                }

            contextVector.entries.sumByDouble { (term, score) ->
                (aspectVector[term] ?: 0.0) * score
//                (aspectVector[term] ?: 0.0)
            }
        }
    }



    /**
     * Sentence: Word2Vec Text -> Candidate Word2Vec
     */
    fun wordToVecFeature(
        fc: FeatureContainer,
        contextType: ContextType<String>,
        aspectFieldType: AspectFieldType<String>
    ): List<Double> = with(fc) {

        val contextEmbedding = wordToVecEmbedding(contextType.field(entry))

        return entry.candidateAspects.map { aspectCandidate ->
            val candidateText = aspectFieldType.field(aspectCandidate)
            val aspectEmbedding = wordToVecEmbedding(candidateText)
            if (contextEmbedding == null || aspectEmbedding == null) 0.0
//            else contextEmbedding.mmul(aspectEmbedding.transpose()).sumNumber().toDouble()
            else contextEmbedding.cosineSim(aspectEmbedding.transpose())
        }
    }

    private fun wordToVecEmbedding(text: String): INDArray? {
        val word2Vec = word2VecRef.value // Lazy initialization
        return try {
            word2Vec.getWordVectorsMean(text.toLowerCase().split(" ")).reshape(1, 300)
        } catch(e: IllegalStateException) {
            // Either doc had no entities or none were in map
            null
        }
    }



}

