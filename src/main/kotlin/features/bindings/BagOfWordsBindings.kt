package features.bindings

import features.BasicFeatures
import features.bindings.FeatureBinding
import features.containers.FeatureContainer
import features.containers.ContextType.*
import features.containers.AspectFieldType.*
import indexing.StatType
import indexing.StatType.*

//enum class SimpBindings(val binding: (FeatureContainer) -> List<Double>) {
//object SimpBindings {
sealed class BagOfWordsBindings  {

    // ------------------------------- Sentence Features ------------------------- ///

    object SENT_CONTENT_TO_ASPECT_CONTENT_TFIDF : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_SENTENCE,
                contextStatType = CONTEXT_TEXT,
                aspectFieldType = ASPECT_FIELD_CONTENT,
                aspectStatType = SECTION_TEXT,
                isBM25 = false
            )
        }
    )

    object SENT_CONTENT_TO_ASPECT_CONTENT_BM25 : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_SENTENCE,
                contextStatType = CONTEXT_TEXT,
                aspectFieldType = ASPECT_FIELD_CONTENT,
                aspectStatType = SECTION_TEXT,
                isBM25 = true
            )
        }
    )

    object SENT_CONTENT_TO_ASPECT_HEADER_TFIDF : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_SENTENCE,
                contextStatType = CONTEXT_TEXT,
                aspectFieldType = ASPECT_FIELD_HEADER,
                aspectStatType = SECTION_HEADER,
                isBM25 = false
            )
        }
    )

    object SENT_CONTENT_TO_ASPECT_HEADER_BM25 : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_SENTENCE,
                contextStatType = CONTEXT_TEXT,
                aspectFieldType = ASPECT_FIELD_HEADER,
                aspectStatType = SECTION_HEADER,
                isBM25 = true
            )
        }
    )

    object SENT_ENTITIES_TO_ASPECT_ENTITIES_TFIDF : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_SENTENCE_ENTITY_STRING,
                contextStatType = CONTEXT_ENTITY,
                aspectFieldType = ASPECT_FIELD_ENTITY_STRING,
                aspectStatType = SECTION_ENTITY,
                isBM25 = false
            )
        }
    )

    object SENT_ENTITIES_TO_ASPECT_ENTITIES_BM25 : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_SENTENCE_ENTITY_STRING,
                contextStatType = CONTEXT_ENTITY,
                aspectFieldType = ASPECT_FIELD_ENTITY_STRING,
                aspectStatType = SECTION_ENTITY,
                isBM25 = false
            )
        }
    )



    // ------------------------------- Simple Paragraph Features ------------------------- ///

    object PARA_CONTENT_TO_ASPECT_CONTENT_TFIDF : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_PARAGRAPH,
                contextStatType = CONTEXT_TEXT,
                aspectFieldType = ASPECT_FIELD_CONTENT,
                aspectStatType = SECTION_TEXT,
                isBM25 = false
            )
        }
    )

    object PARA_CONTENT_TO_ASPECT_CONTENT_BM25 : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_PARAGRAPH,
                contextStatType = CONTEXT_TEXT,
                aspectFieldType = ASPECT_FIELD_CONTENT,
                aspectStatType = SECTION_TEXT,
                isBM25 = true
            )
        }
    )

    object PARA_CONTENT_TO_ASPECT_CONTENT_WORD2Vec : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.wordToVecFeature(
                featureContainer,
                contextType = CONTEXT_TYPE_PARAGRAPH,
                aspectFieldType = ASPECT_FIELD_CONTENT
            )
        }
    )

    object PARA_CONTENT_TO_ASPECT_HEADER_TFIDF : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_PARAGRAPH,
                contextStatType = CONTEXT_TEXT,
                aspectFieldType = ASPECT_FIELD_HEADER,
                aspectStatType = SECTION_HEADER,
                isBM25 = false
            )
        }
    )

    object PARA_CONTENT_TO_ASPECT_HEADER_BM25 : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_PARAGRAPH,
                contextStatType = CONTEXT_TEXT,
                aspectFieldType = ASPECT_FIELD_HEADER,
                aspectStatType = SECTION_HEADER,
                isBM25 = true
            )
        }
    )

    object PARA_ENTITIES_TO_ASPECT_ENTITIES_TFIDF : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_PARAGRAPH_ENTITY_STRING,
                contextStatType = CONTEXT_ENTITY,
                aspectFieldType = ASPECT_FIELD_ENTITY_STRING,
                aspectStatType = SECTION_ENTITY,
                isBM25 = false
            )
        }
    )

    object PARA_ENTITIES_TO_ASPECT_ENTITIES_BM25 : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.tfidfOrBm25(
                featureContainer,
                contextType = CONTEXT_TYPE_PARAGRAPH_ENTITY_STRING,
                contextStatType = CONTEXT_ENTITY,
                aspectFieldType = ASPECT_FIELD_ENTITY_STRING,
                aspectStatType = SECTION_ENTITY,
                isBM25 = false
            )
        }
    )

}