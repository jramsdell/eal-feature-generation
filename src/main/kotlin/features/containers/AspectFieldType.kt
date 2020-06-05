package features.containers

import json.containers.Aspect
import json.containers.AspectLinkExample
import json.containers.EntityMention

//import utils.lucene.indexer.containers.AspectCandidate

sealed class AspectFieldType<R>(open val field: (Aspect) -> R) {
    object ASPECT_FIELD_CONTENT: AspectFieldType<String>({ it.aspectContent.content })
    object ASPECT_FIELD_ENTITY: AspectFieldType<List<EntityMention>>({ it.aspectContent.entities })
    object ASPECT_FIELD_ENTITY_STRING: AspectFieldType<String>({
        it.aspectContent.entities.map { it.entityName }.joinToString(" ") })
    object ASPECT_FIELD_HEADER: AspectFieldType<String>({ it.location.sectionId.first() })
}
