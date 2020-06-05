package indexing

enum class StatType(val field: String, val isAspect: Boolean) {
    CONTEXT_TEXT("contextText", false),
    CONTEXT_ENTITY("contextEntity", false),
    SECTION_TEXT("sectionText", true),
    SECTION_HEADER("sectionHeader", true),
    SECTION_ENTITY("sectionEntities", true)
}