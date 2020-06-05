package features.containers

import json.containers.AspectLinkExample
import json.containers.EntityMention

//enum class ContextType(val field: (AspectLinkExample) -> String) {
//    CONTEXT_TYPE_SENTENCE(field = { it.context.sentence.content }),
//    CONTEXT_TYPE_SENTENCE_ENTITY(field = { it.context.sentence.entities }),
//    CONTEXT_TYPE_PARAGRAPH(field = { it.context.paragraph.content }),
//    CONTEXT_TYPE_PARAGRAPH_ENTITY(field = { it.context.paragraph.entities}),
//    CONTEXT_TYPE_SECTION(field = { it.sectContext.content }),
//    CONTEXT_TYPE_SECTION_ENTITY(field = { it.sectContext.entities.joinToString(" ") }),
//}

sealed class ContextType<R>(open val field: (AspectLinkExample) -> R) {
    object CONTEXT_TYPE_SENTENCE : ContextType<String>({ it.context.sentence.content })
    object CONTEXT_TYPE_SENTENCE_ENTITY : ContextType<List<EntityMention>>({ it.context.sentence.entities })
    object CONTEXT_TYPE_SENTENCE_ENTITY_STRING : ContextType<String>({
        it.context.sentence.entities.map { it.entityName }.joinToString(" ") })
    object CONTEXT_TYPE_PARAGRAPH : ContextType<String>({ it.context.paragraph.content })
    object CONTEXT_TYPE_PARAGRAPH_ENTITY : ContextType<List<EntityMention>>({ it.context.paragraph.entities })
    object CONTEXT_TYPE_PARAGRAPH_ENTITY_STRING : ContextType<String>({
        it.context.paragraph.entities.map { it.entityName }.joinToString(" ") })
}