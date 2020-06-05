package features.bindings

import features.BasicFeatures
import features.bindings.FeatureBinding
import features.containers.FeatureContainer
import features.containers.ContextType.*
import features.containers.AspectFieldType.*

//enum class SimpBindings(val binding: (FeatureContainer) -> List<Double>) {
//object SimpBindings {
sealed class SimpBindings  {

    // ------------------------------- Simple Sentence Features ------------------------- ///

    object SIMP_SENT_CONTENT_OVERLAP : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.simpleHeuristicContentOverlap(
                featureContainer,
                contextType = CONTEXT_TYPE_SENTENCE,
                aspectFieldType = ASPECT_FIELD_CONTENT
            )
        }
    )

    object SIMP_SENT_ENTITIES_OVERLAP : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.simpleHeuristicEntityOverlap(
                featureContainer,
                contextType = CONTEXT_TYPE_SENTENCE_ENTITY,
                aspectFieldType = ASPECT_FIELD_ENTITY
            )
        }
    )

    // ------------------------------- Simple Paragraph Features ------------------------- ///

    object SIMP_PARA_CONTENT_OVERLAP : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.simpleHeuristicContentOverlap(
                featureContainer,
                contextType = CONTEXT_TYPE_PARAGRAPH,
                aspectFieldType = ASPECT_FIELD_CONTENT
            )
        }
    )

    object SIMP_PARA_ENTITIES_OVERLAP : FeatureBinding(
        binding = { featureContainer ->
            BasicFeatures.simpleHeuristicEntityOverlap(
                featureContainer,
                contextType = CONTEXT_TYPE_PARAGRAPH_ENTITY,
                aspectFieldType = ASPECT_FIELD_ENTITY
            )
        }
    )

}